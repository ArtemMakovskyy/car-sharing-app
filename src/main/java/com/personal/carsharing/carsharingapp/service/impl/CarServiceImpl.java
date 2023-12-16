package com.personal.carsharing.carsharingapp.service.impl;

import com.personal.carsharing.carsharingapp.dto.internal.car.CarDto;
import com.personal.carsharing.carsharingapp.dto.internal.car.CreateCarRequestDto;
import com.personal.carsharing.carsharingapp.dto.mapper.CarMapper;
import com.personal.carsharing.carsharingapp.exception.EntityNotFoundException;
import com.personal.carsharing.carsharingapp.model.Car;
import com.personal.carsharing.carsharingapp.repository.car.CarRepository;
import com.personal.carsharing.carsharingapp.service.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarRequestDto requestDto) {
        final Car car = carMapper.fromCreateDtoToEntity(requestDto);
        final Car savedCar = carRepository.save(car);
        return carMapper.toDto(savedCar);
    }

    @Override
    public List<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable)
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto findById(Long id) {
        return carMapper.toDto(carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't get car bi id " + id)));
    }

    @Override
    public CarDto update(Long id, CreateCarRequestDto requestDto) {
        if (carRepository.existsById(id)) {
            Car car = carMapper.fromCreateDtoToEntity(requestDto);
            car.setId(id);
            Car savedCar = carRepository.save(car);
            return carMapper.toDto(savedCar);
        }
        throw new EntityNotFoundException("Car by id " + id + " does not exist");
    }

    @Override
    public boolean isDeleteById(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Car by id " + id + " does not exist");
        }
        carRepository.deleteById(id);
        return true;
    }
}
