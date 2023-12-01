package com.personal.carsharing.carsharingapp.service.impl;

import com.personal.carsharing.carsharingapp.dto.internal.car.CarDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.CreatePaymentSessionDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentResponseDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.dto.mapper.CarMapper;
import com.personal.carsharing.carsharingapp.dto.mapper.PaymentMapper;
import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.exception.EntityNotFoundException;
import com.personal.carsharing.carsharingapp.exception.StripeProcessException;
import com.personal.carsharing.carsharingapp.model.Car;
import com.personal.carsharing.carsharingapp.model.Payment;
import com.personal.carsharing.carsharingapp.repository.car.CarRepository;
import com.personal.carsharing.carsharingapp.repository.payment.PaymentRepository;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.SetupIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Log4j2
public class StripeServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.2");
    private static final String BASE_URL = "http://localhost";
    private static final int PORT = 8080;
    private static final String SUCCESS_PAYMENT_URL_PATH = "/api/payments/success";
    private static final String CANCEL_PAYMENT_URL_PATH = "/api/payments/cancel";
    //    private static final String SESSION_AUTO_REQUEST = "?session_id={CHECKOUT_SESSION_ID}";
    //    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    //    private static final Long EXPIRATION_TIME_IN_SECONDS = 85800L;
    //    private static final Long SECOND_DIVIDE = 1000L;
    private static final Long DEFAULT_CAR_QUANTITY_FOR_RENTING = 1L;
    private static final BigDecimal SMALL_CHANGE_IS_IN_BANKNOTE = new BigDecimal(100);
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Value("${api.stripe.sk.key}")
    private String stripeKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeKey;
    }

    @Override
    public PaymentResponseDto createPaymentSession(
            CreatePaymentSessionDto createPaymentSessionDto) {
        Map<String, Object> rentalPrice = new HashMap<>();
        rentalPrice.put("price", createPriceFromRentalThenGetPriceId(
                createPaymentSessionDto.rentalId()));
        rentalPrice.put("quantity", DEFAULT_CAR_QUANTITY_FOR_RENTING);

        List<Object> lineItems = new ArrayList<>();
        lineItems.add(rentalPrice);

        Map<String, Object> params = new HashMap<>();
        params.put("success_url", buildUrl(SUCCESS_PAYMENT_URL_PATH));
        params.put("cancel_url", buildUrl(CANCEL_PAYMENT_URL_PATH));
        params.put("line_items", lineItems);
        params.put("mode", SessionCreateParams.Mode.PAYMENT);
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");
        params.put("payment_method_types", paymentMethodTypes);
        try {
            Session session = Session.create(params);
            log.info("Session created with link: " + session.getUrl());
            return createPayment(session, createPaymentSessionDto);
        } catch (StripeException e) {
            throw new StripeProcessException("Can't create pay session ", e);
        }
    }

    private String buildUrl(String path) {
        return UriComponentsBuilder.fromUriString(BASE_URL)
                .port(PORT)
                .path(path)
                .build()
                .toString();
    }

    private PaymentResponseDto createPayment(
            Session session, CreatePaymentSessionDto createPaymentSessionDto) {
        Payment payment = new Payment();
        payment.setSessionId(session.getId());
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.valueOf(createPaymentSessionDto.paymentType().toUpperCase()));
        payment.setRental(rentalRepository.findById(createPaymentSessionDto.rentalId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental by id "
                        + createPaymentSessionDto.rentalId())));
        payment.setAmountToPay(new BigDecimal(session.getAmountTotal())
                .divide(SMALL_CHANGE_IS_IN_BANKNOTE, 2, RoundingMode.DOWN));
        try {
            payment.setSessionUrl(new URL(session.getUrl()));
            return paymentMapper.toDto(paymentRepository.save(payment));
        } catch (MalformedURLException e) {
            throw new StripeProcessException("Can't add url to payment", e);
        }
    }

    private String createPriceFromRentalThenGetPriceId(Long rentalId) {
        Map<String, Object> params = new HashMap<>();
        params.put("unit_amount", unitAmountCalculation(rentalId));
        params.put("currency", SetupIntentCreateParams.PaymentMethodOptions.AcssDebit.Currency.USD);
        params.put("product", createProductAndGetIdByRentalId(rentalId));
        try {
            Price price = Price.create(params);
            log.info("price id:" + price.getId());
            return price.getId();
        } catch (StripeException e) {
            throw new StripeProcessException("Can't create price ", e);
        }
    }

    private int unitAmountCalculation(Long rentalId) {
        final RentalDto rentalDto = rentalMapper.toDto(rentalRepository
                .findById(rentalId).orElseThrow());
        final Car car = carRepository.findById(rentalDto.getCarId()).orElseThrow();

        if (rentalDto.getReturnDate().isEqual(rentalDto.getActualReturnDate())
                || rentalDto.getActualReturnDate().isBefore(rentalDto.getReturnDate())) {
            BigDecimal daysInRent = daysInRent(rentalDto.getRentalDate(),
                    rentalDto.getActualReturnDate(), true);
            return calculationPriceByPeriodAndIsFine(daysInRent, car.getDailyFee(), false);
        }
        BigDecimal daysInRentWithoutFine = daysInRent(rentalDto.getRentalDate(),
                rentalDto.getReturnDate(), true);
        BigDecimal daysInRentWithFine = daysInRent(rentalDto.getReturnDate(),
                rentalDto.getActualReturnDate(), false);
        return calculationPriceByPeriodAndIsFine(daysInRentWithoutFine, car.getDailyFee(), false)
                + calculationPriceByPeriodAndIsFine(daysInRentWithFine, car.getDailyFee(), true);
    }

    private BigDecimal daysInRent(LocalDate start, LocalDate end, boolean isAddDay) {
        if (isAddDay) {
            end = end.plusDays(1);
        }
        return new BigDecimal(ChronoUnit.DAYS.between(start, end));
    }

    private int calculationPriceByPeriodAndIsFine(
            BigDecimal period, BigDecimal dailyFee, boolean isFine) {
        BigDecimal price = period.multiply(dailyFee).multiply(SMALL_CHANGE_IS_IN_BANKNOTE);
        if (isFine) {
            price = price.multiply(FINE_MULTIPLIER);
        }
        return price.intValue();
    }

    private String createProductAndGetIdByRentalId(Long rentalId) {
        final RentalDto rentalDto = rentalMapper.toDto(rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Can't get rental by id "
                        + rentalId)));
        final CarDto carDto = carMapper.toDto(carRepository.findById(rentalDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Can't get car by id "
                        + rentalDto.getCarId())));
        Map<String, Object> productParams = new HashMap<>();
        productParams.put("name", "rental_id:" + rentalDto.getId());
        productParams.put("description", createDescription(rentalDto, carDto));

        try {
            Product product = Product.create(productParams);
            log.info("product id:" + product.getId());
            return product.getId();
        } catch (StripeException e) {
            throw new StripeProcessException("Can't create product ", e);
        }
    }

    private String createDescription(RentalDto rentalDto, CarDto carDto) {
        final String description = String.format(
                "Rental car: %s %s, type %s. Was in rent from %s to %s", carDto.brand(),
                carDto.model(), carDto.type(), rentalDto.getRentalDate(),
                rentalDto.getActualReturnDate());
        if (rentalDto.getActualReturnDate().isAfter(rentalDto.getReturnDate())) {
            final String fineDescription = String.format(
                    ", of these days, the last %s days with a fine - 20 percents.",
                    daysInRent(rentalDto.getReturnDate(), rentalDto.getActualReturnDate(), false));
            return description + fineDescription;
        }
        return description;
    }
}
