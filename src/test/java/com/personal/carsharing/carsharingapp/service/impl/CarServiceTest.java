package com.personal.carsharing.carsharingapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.personal.carsharing.carsharingapp.dto.internal.car.CarDto;
import com.personal.carsharing.carsharingapp.dto.internal.car.CreateCarRequestDto;
import com.personal.carsharing.carsharingapp.dto.mapper.CarMapper;
import com.personal.carsharing.carsharingapp.exception.EntityNotFoundException;
import com.personal.carsharing.carsharingapp.model.Car;
import com.personal.carsharing.carsharingapp.model.CarType;
import com.personal.carsharing.carsharingapp.repository.car.CarRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    private static final Long INVALID_NON_EXISTING_ID = 1000L;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @DisplayName("Save car by valid car and return carDto")
    @Test
    public void save_validCar_ReturnCarDto() {
        //given
        Car car = getCarAudiA8();
        final CreateCarRequestDto carRequestDto = toCreateCarRequestDto(car);
        final CarDto expected = toCarDto(car);

        Mockito.when(carMapper.fromCreateDtoToEntity(carRequestDto)).thenReturn(car);
        Mockito.when(carRepository.save(car)).thenReturn(car);
        Mockito.when(carMapper.toDto(car)).thenReturn(expected);

        //when
        final CarDto actual = carService.save(carRequestDto);

        //then
        assertEquals(expected, actual);
        Mockito.verify(carMapper, Mockito.times(1)).fromCreateDtoToEntity(carRequestDto);
        Mockito.verify(carRepository, Mockito.times(1)).save(car);
        Mockito.verify(carMapper, Mockito.times(1)).toDto(car);
    }

    @DisplayName("Verify findAll method works")
    @Test
    public void findAll_ValidPageable_ShouldReturnAllCars() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        List<Car> cars = List.of(getCarAudiA8());
        Page<Car> carPage = new PageImpl<>(cars, pageable, cars.size());

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(any())).thenReturn(toCarDto(getCarAudiA8()));

        //when
        List<CarDto> carDtos = carService.findAll(pageable);

        //then
        assertEquals(carDtos.size(), 1);
        assertTrue(carDtos.contains(toCarDto(getCarAudiA8())));
        verify(carRepository, times(1)).findAll(pageable);
        verify(carMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @DisplayName("Find existing car by valid ID then return carDto")
    @Test
    public void findById_ValidCarId_ReturnCarDto() {
        //given
        Long carId = anyLong();
        Car car = getCarAudiA8();
        CarDto expected = toCarDto(car);

        Mockito.when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        Mockito.when(carMapper.toDto(car)).thenReturn(expected);

        //when
        final CarDto actual = carService.findById(carId);

        //then
        assertEquals(expected, actual);
        Mockito.verify(carRepository, Mockito.times(1)).findById(carId);
        Mockito.verify(carMapper, Mockito.times(1)).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @DisplayName("Find non existing car by invalid ID then throw EntityNotFoundException")
    @Test
    public void findById_InvalidCarId_ThrowException() {
        //given
        Mockito.when(carRepository.findById(INVALID_NON_EXISTING_ID))
                .thenThrow(new EntityNotFoundException(
                        "Can't get car bi id " + INVALID_NON_EXISTING_ID));

        //when
        final EntityNotFoundException actualException =
                assertThrows(EntityNotFoundException.class,
                        () -> carService.findById(INVALID_NON_EXISTING_ID));

        //then
        assertEquals("Can't get car bi id "
                + INVALID_NON_EXISTING_ID, actualException.getMessage());
        verify(carRepository, times(1))
                .findById(INVALID_NON_EXISTING_ID);
        verifyNoMoreInteractions(carRepository);
    }

    @DisplayName("Update existing car by valid car. Should return new carDto")
    @Test
    public void update_ValidCarIdAndCar_shouldReturnUpdatedCar() {
        //given
        Long existingCarId = getCarAudiA8().getId();
        CreateCarRequestDto createRequestDto = toCreateCarRequestDto(getCarAudiA8());

        when(carRepository.existsById(existingCarId)).thenReturn(true);
        when(carMapper.fromCreateDtoToEntity(createRequestDto)).thenReturn(getCarAudiA8());
        when(carRepository.save(any(Car.class))).thenReturn(getCarAudiA8());
        when(carMapper.toDto(any(Car.class))).thenReturn(toCarDto(getCarAudiA8()));

        //when
        final CarDto actual = carService.update(getCarAudiA8().getId(), createRequestDto);

        //then
        CarDto expected = toCarDto(getCarAudiA8());
        assertEquals(expected, actual);
        verify(carRepository, times(1)).existsById(existingCarId);
        verify(carMapper, times(1)).fromCreateDtoToEntity(createRequestDto);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(carMapper, times(1)).toDto(any(Car.class));
    }

    @DisplayName("Update car by invalid car ID, throw EntityNotFoundException")
    @Test
    public void update_InvalidCarId_shouldReturnException() {
        //given
        CreateCarRequestDto requestDto = toCreateCarRequestDto(getCarAudiA8());
        when(carRepository.existsById(INVALID_NON_EXISTING_ID)).thenReturn(false);

        //when
        EntityNotFoundException actualException =
                assertThrows(EntityNotFoundException.class, () -> {
                    carService.update(INVALID_NON_EXISTING_ID, requestDto);
                });

        //then
        assertEquals("Car by id " + INVALID_NON_EXISTING_ID
                + " does not exist", actualException.getMessage());
        verify(carRepository, times(1))
                .existsById(INVALID_NON_EXISTING_ID);
        verify(carRepository, never()).save(any(Car.class));
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Delete car by valid id. Should return true")
    public void isDeleteById_ValidId_ShouldReturnTrue() {
        // Given
        Long carId = anyLong();

        when(carRepository.existsById(carId)).thenReturn(true);

        // when
        final boolean result = carService.isDeleteById(anyLong());

        // then
        assertTrue(result);
        verify(carRepository, times(1)).deleteById(carId);
    }

    @Test
    @DisplayName("Make sure that an exception is thrown when passing a invalid car ID.")
    public void isDeleteById_InvalidId_ThrowEntityNotFoundException() {
        //given
        Mockito.when(carRepository.existsById(INVALID_NON_EXISTING_ID))
                .thenThrow(new EntityNotFoundException(
                        "Car by id  " + INVALID_NON_EXISTING_ID + "  does not exist"));
        String expected = "Car by id  " + INVALID_NON_EXISTING_ID + "  does not exist";

        //when
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> carService.isDeleteById(INVALID_NON_EXISTING_ID));

        //then
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(carRepository, times(1))
                .existsById(INVALID_NON_EXISTING_ID);
        verifyNoMoreInteractions(carRepository);
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

    private CreateCarRequestDto toCreateCarRequestDto(Car car) {
        return new CreateCarRequestDto(
                car.getModel(),
                car.getBrand(),
                car.getType().name(),
                car.getInventory(),
                car.getDailyFee());
    }

    private Car getCarAudiA8() {
        Car car = new Car();
        car.setId(100L);
        car.setModel("A8");
        car.setBrand("Audi");
        car.setType(CarType.SEDAN);
        car.setInventory(2);
        car.setDailyFee(new BigDecimal("25.50"));
        car.setDeleted(false);
        return car;
    }
}
