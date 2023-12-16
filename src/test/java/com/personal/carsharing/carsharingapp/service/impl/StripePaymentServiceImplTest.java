package com.personal.carsharing.carsharingapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.personal.carsharing.carsharingapp.dto.internal.car.CarDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.CreatePaymentSessionDto;
import com.personal.carsharing.carsharingapp.dto.internal.payment.PaymentResponseDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.CreateRentalRequestDto;
import com.personal.carsharing.carsharingapp.dto.internal.rental.RentalDto;
import com.personal.carsharing.carsharingapp.dto.mapper.CarMapper;
import com.personal.carsharing.carsharingapp.dto.mapper.PaymentMapper;
import com.personal.carsharing.carsharingapp.dto.mapper.RentalMapper;
import com.personal.carsharing.carsharingapp.model.Car;
import com.personal.carsharing.carsharingapp.model.CarType;
import com.personal.carsharing.carsharingapp.model.Payment;
import com.personal.carsharing.carsharingapp.model.PaymentStatus;
import com.personal.carsharing.carsharingapp.model.PaymentType;
import com.personal.carsharing.carsharingapp.model.Rental;
import com.personal.carsharing.carsharingapp.model.User;
import com.personal.carsharing.carsharingapp.repository.car.CarRepository;
import com.personal.carsharing.carsharingapp.repository.payment.PaymentRepository;
import com.personal.carsharing.carsharingapp.repository.rental.RentalRepository;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceHandler;
import com.personal.carsharing.carsharingapp.service.strategy.payment.PriceStrategy;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
class StripePaymentServiceImplTest {
    private static final Pageable PAGE_REQUEST = PageRequest.of(0, 10);
    private static final String PAYMENT_TYPE = "PAYMENT";
    private static final String EXPECTED_MESSAGE = "The rent payment was successful";
    private static final String SESSION_ID = "test-session-id";
    private static final Long FIRST_ID = 1L;
    private static final int TOTAL_PRICE = 220;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PriceStrategy priceStrategy;

    @Mock
    private PriceHandler priceHandler;

    @InjectMocks
    private StripePaymentServiceImpl paymentService;

    @Value("${api.stripe.sk.key}")
    private String stripeKey = "sk_test_51O9wU7BPFbfBPNcAepBx4bzUCPytFCW8L"
            + "7n1izlWQuBH94p9IRfsGumwe99do3UJL8okpaXaxiAWrAT1aovcyPyN003ZLWY98D";

    private static Object answer(InvocationOnMock invocation) {
        return invocation.getArgument(0);
    }

    @DisplayName("Find all rental by id. Return PaymentResponseDtos")
    @Test
    void findAllByRentalUserId_ValidData_ReturnPaymentResponseDto()
            throws MalformedURLException {
        //given
        Rental rental = getRental(getCar(), getUser(),
                getCreateRentalRequestDto(getCar().getId()));
        Payment payment = getPayment(rental);
        Page<Payment> paymentPage = new PageImpl<>(
                List.of(payment), PAGE_REQUEST, List.of(payment).size());

        when(paymentRepository.findAllByRentalUserId(
                PAGE_REQUEST, getUser().getId())).thenReturn(paymentPage);
        when(paymentMapper.toDto(any(Payment.class)))
                .thenReturn(getPaymentResponseDto(payment, rental));

        List<PaymentResponseDto> expected =
                List.of(getPaymentResponseDto(payment, rental));
        //when
        List<PaymentResponseDto> actual = paymentService
                .findAllByRentalUserId(getUser().getId(), PAGE_REQUEST);

        //then
        assertEquals(expected, actual);
        verify(paymentRepository, times(1))
                .findAllByRentalUserId(PAGE_REQUEST, getUser().getId());
        verify(paymentMapper, times(1))
                .toDto(any(Payment.class));
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @DisplayName("Create payment with valid CreatePaymentSessionDto. Return PaymentResponseDto")
    @Test
    void createPaymentSession_ValidData_ReturnPaymentResponseDto()
            throws StripeException, MalformedURLException {
        // Given
        Stripe.apiKey = stripeKey;
        Rental rental = getRental(getCar(), getUser(),
                getCreateRentalRequestDto(getCar().getId()));
        CreatePaymentSessionDto createPaymentSessionDto =
                new CreatePaymentSessionDto(rental.getId(), PAYMENT_TYPE);
        Payment payment = getPayment(rental);

        when(rentalRepository.findById(anyLong())).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(any())).thenReturn(toRentalDto(rental));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(getCar()));
        when(carMapper.toDto(any())).thenReturn(toCarDto(getCar()));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(priceStrategy.get(PaymentType.PAYMENT)).thenReturn(priceHandler);
        when(priceHandler.getTotalPrice(any(), any(), any(), any())).thenReturn(TOTAL_PRICE);
        when(paymentMapper.toDto(any())).thenReturn(getPaymentResponseDto(payment, rental));

        PaymentResponseDto expectedResponseDto =
                getPaymentResponseDto(payment, rental);

        // When
        PaymentResponseDto actualResponseDto = paymentService
                .createPaymentSession(createPaymentSessionDto);

        // Then
        assertNotNull(actualResponseDto);
        assertEquals(expectedResponseDto, actualResponseDto);
        verify(rentalRepository, times(3)).findById(anyLong());
        verify(rentalMapper, times(2)).toDto(any());
        verify(carRepository, times(2)).findById(anyLong());
        verify(carMapper, times(1)).toDto(any());
        verify(paymentRepository, times(1)).save(any());
        verify(priceStrategy, times(1)).get(any());
        verify(priceHandler, times(1)).getTotalPrice(any(), any(), any(), any());
        verify(paymentMapper, times(1)).toDto(any());

    }

    @DisplayName("Handle Successful Payment")
    @Test
    void handleSuccessfulPayment_PaymentFound_UpdateStatusAndReturnMessage()
            throws MalformedURLException {
        // Given
        Payment payment = getPayment(
                getRental(getCar(),
                        getUser(),
                        getCreateRentalRequestDto(getCar().getId())));

        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(StripePaymentServiceImplTest::answer);

        // When
        String result = paymentService.handleSuccessfulPayment(SESSION_ID);

        // Then
        assertEquals(EXPECTED_MESSAGE, result);
        assertEquals(PaymentStatus.PAID, payment.getStatus());
        verify(paymentRepository, times(1)).findBySessionId(SESSION_ID);
        verify(paymentRepository, times(1)).save(payment);
        verifyNoMoreInteractions(paymentRepository);
    }

    private RentalDto toRentalDto(Rental rental) {
        RentalDto rentalDto = new RentalDto();
        rentalDto.setUserId(rental.getUser().getId());
        rentalDto.setRentalDate(rental.getRentalDate());
        rentalDto.setReturnDate(rental.getReturnDate());
        rentalDto.setActualReturnDate(rental.getActualReturnDate());
        rentalDto.setCarId(rental.getCar().getId());
        rentalDto.setUserId(rental.getUser().getId());
        return rentalDto;
    }

    private Payment getPayment(Rental rental) throws MalformedURLException {
        Payment payment = new Payment();
        payment.setId(FIRST_ID);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setType(PaymentType.PAYMENT);
        payment.setRental(rental);
        payment.setSessionUrl(new URL("https://session.url.com"));
        payment.setSessionId("session.id");
        payment.setAmountToPay(BigDecimal.valueOf(TOTAL_PRICE));
        return payment;
    }

    private PaymentResponseDto getPaymentResponseDto(Payment payment, Rental rental) {
        return new PaymentResponseDto(
                "payment-test-id" + rental.getId(),
                payment.getStatus().name(),
                payment.getType().name(),
                rental.getId(),
                payment.getSessionId(),
                TOTAL_PRICE,
                payment.getSessionUrl().toString()
        );
    }

    private CarDto toCarDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getType().name(),
                car.getInventory(),
                car.getDailyFee());
    }

    private Car getCar() {
        Car car = new Car();
        car.setId(5L);
        car.setModel("Camry");
        car.setBrand("Toyota");
        car.setType(CarType.SEDAN);
        car.setInventory(1);
        car.setDailyFee(BigDecimal.valueOf(TOTAL_PRICE));
        car.setDeleted(false);
        return car;
    }

    private Rental getRental(Car car, User user, CreateRentalRequestDto requestDto) {
        Rental rental = new Rental();
        rental.setId(FIRST_ID);
        rental.setRentalDate(requestDto.getRentalDate());
        rental.setReturnDate(requestDto.getReturnDate());
        rental.setActualReturnDate(requestDto.getReturnDate());
        rental.setCar(car);
        rental.setUser(user);
        rental.setDeleted(false);
        rental.setActive(true);
        return rental;
    }

    private User getUser() {
        User user = new User();
        user.setId(FIRST_ID);
        user.setEmail("first.user1234@email.com");
        user.setFirstName("FirstUserFirstName");
        user.setLastName("FirstUserLastName");
        user.setPassword("1234");
        user.setDeleted(false);
        return user;
    }

    private CreateRentalRequestDto getCreateRentalRequestDto(Long carId) {
        CreateRentalRequestDto requestDto = new CreateRentalRequestDto();
        requestDto.setRentalDate(LocalDate.now().plusDays(1L));
        requestDto.setReturnDate(LocalDate.now().plusDays(14L));
        requestDto.setCarId(carId);
        return requestDto;
    }
}
