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
import com.personal.carsharing.carsharingapp.model.PaymentStatus;
import com.personal.carsharing.carsharingapp.model.PaymentType;
import com.personal.carsharing.carsharingapp.repository.car.CarRepository;
import com.personal.carsharing.carsharingapp.repository.payment.PaymentRepository;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.service.PaymentService;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceHandler;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceStrategy;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Log4j2
public class StripePaymentServiceImpl implements PaymentService {
    private static final String BASE_URL = "http://localhost";
    private static final int PORT = 8080;
    private static final String SUCCESS_PAYMENT_URL_PATH = "/api/payments/success";
    private static final String CANCEL_PAYMENT_URL_PATH = "/api/payments/cancel";
    private static final String QUERY_NAME_PARAM = "session_id";
    private static final String QUERY_VALUE_PARAM = "{CHECKOUT_SESSION_ID}";
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.2");
    private static final Long DEFAULT_CAR_QUANTITY_FOR_RENTING = 1L;
    private static final BigDecimal SMALL_CHANGE_IS_IN_BANKNOTE = new BigDecimal(100);
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PriceStrategy priceStrategy;

    @Value("${api.stripe.sk.key}")
    private String stripeKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeKey;
    }

    @Override
    public List<PaymentResponseDto> findAllByRentalUserId(Long userId, Pageable pageable) {
        return paymentRepository.findAllByRentalUserId(pageable, userId)
                .map(paymentMapper::toDto).toList();
    }

    @Override
    public PaymentResponseDto createPaymentSession(
            CreatePaymentSessionDto createPaymentSessionDto) {
        final SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(
                                        createPriceFromRentalThenGetPriceId(
                                                createPaymentSessionDto)
                                )
                                .setQuantity(DEFAULT_CAR_QUANTITY_FOR_RENTING)
                                .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(buildUrl(SUCCESS_PAYMENT_URL_PATH))
                .setCancelUrl(buildUrl(CANCEL_PAYMENT_URL_PATH))
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .build();
        try {
            Session session = Session.create(params);
            log.info(session);
            return createPayment(session, createPaymentSessionDto);
        } catch (StripeException e) {
            throw new StripeProcessException("Can't create pay session ", e);
        }
    }

    public String handleSuccessfulPayment(String sessionId) {
        final Payment paymentFromDb = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find payment by session ID " + sessionId));
        paymentFromDb.setStatus(PaymentStatus.PAID);
        paymentRepository.save(paymentFromDb);
        return "The rent payment was successful";
    }

    @Override
    public String processPaymentCancellation(String sessionId) {
        return "The payment can be made later (but the session is available for only 24 hours). "
                + "Payment validity period ends at "
                + getEndSessionDateTimeBySessionId(sessionId) + ".";
    }

    private String getEndSessionDateTimeBySessionId(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            final Long expiresAt = session.getExpiresAt();
            Instant instant = Instant.ofEpochSecond(expiresAt);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return dateTime.format(DATE_TIME_FORMAT);
        } catch (StripeException e) {
            throw new StripeProcessException(
                    "Can't get expiration time by session ID: " + sessionId, e);
        }
    }

    private String createPriceFromRentalThenGetPriceId(
            CreatePaymentSessionDto createPaymentSessionDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("unit_amount", unitAmountCalculation(createPaymentSessionDto));
        params.put("currency", SetupIntentCreateParams.PaymentMethodOptions.AcssDebit.Currency.USD);
        params.put("product", createProductAndGetIdByRentalId(createPaymentSessionDto.rentalId()));
        try {
            Price price = Price.create(params);
            return price.getId();
        } catch (StripeException e) {
            throw new StripeProcessException("Can't create price", e);
        }
    }

    private int unitAmountCalculation(CreatePaymentSessionDto createPaymentSessionDto) {
        final PaymentType paymentType =
                PaymentType.valueOf(createPaymentSessionDto.paymentType());
        final PriceHandler priceHandler =
                priceStrategy.get(paymentType);

        final RentalDto rentalDto = rentalMapper.toDto(rentalRepository
                .findById(createPaymentSessionDto.rentalId()).orElseThrow(
                        () -> new EntityNotFoundException("Can't find rental by id "
                                + createPaymentSessionDto.rentalId())));

        final Car car = carRepository.findById(rentalDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id" + rentalDto.getCarId()));

        return priceHandler.getTotalPrice(
                rentalDto, car.getDailyFee(), FINE_MULTIPLIER, SMALL_CHANGE_IS_IN_BANKNOTE);
    }

    private String buildUrl(String path) {
        return UriComponentsBuilder.fromUriString(BASE_URL)
                .port(PORT)
                .path(path)
                .queryParam(QUERY_NAME_PARAM, QUERY_VALUE_PARAM)
                .build()
                .toString();
    }

    private PaymentResponseDto createPayment(
            Session session, CreatePaymentSessionDto createPaymentSessionDto) {
        Payment payment = new Payment();
        payment.setSessionId(session.getId());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setType(PaymentType.valueOf(createPaymentSessionDto.paymentType().toUpperCase()));
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
        return description + ".";
    }

    private BigDecimal daysInRent(LocalDate start, LocalDate end, boolean isAddDay) {
        if (isAddDay) {
            end = end.plusDays(1);
        }
        return new BigDecimal(ChronoUnit.DAYS.between(start, end));
    }
}
