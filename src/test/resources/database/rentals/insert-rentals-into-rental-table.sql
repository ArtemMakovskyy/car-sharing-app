INSERT into rentals(id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted, is_active)
values (1, '2023-12-10', '2023-12-20', '2023-12-20', 9, 3, false, false);

INSERT into rentals(id, rental_date, return_date, car_id, user_id, is_deleted, is_active)
values (2, '2023-12-11', '2023-12-21', 10, 4, true , true);

INSERT into rentals(id, rental_date, return_date, car_id, user_id, is_deleted, is_active)
values (3, '2023-12-12', '2023-12-22', 11, 5, false, true);

INSERT into rentals(id, rental_date, return_date, car_id, user_id, is_deleted, is_active)
values (4, '2023-12-12', '2023-12-22', 1, 6, false, true);