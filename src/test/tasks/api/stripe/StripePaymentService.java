package com.personal.carsharing.carsharingapp.tasks.api.stripe;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StripePaymentService {
    // TODO: 07.11.2023 Затем создайте класс, который будет отвечать за создание сессии платежа:
    /*
    В данном примере создается сессия платежа с одним продуктом стоимостью $10.
    После успешного платежа, пользователь будет перенаправлен на success_url, в
    случае отмены платежа - на cancel_url.

    Это простой пример создания сессии платежа на Stripe с использованием Java.
    Убедитесь, что вы используете свои тестовые ключи API и настраиваете параметры
    сессии согласно вашим требованиям. Не забудьте также обработать ошибки и добавить
    дополнительные параметры, если это необходимо для вашего проекта.
     */
    static {
        Stripe.apiKey = "ваш_тестовый_secret_key";
    }

    public Session createCheckoutSession() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("payment_method_types", List.of("card"));
            params.put("line_items", List.of(Map.of(
                    "price_data", Map.of(
                            "currency", "usd",
                            "product_data", Map.of(
                                    "name", "Пример продукта"
                            ),
                            "unit_amount", 1000 // Сумма в центах (в данном случае $10)
                    ),
                    "quantity", 1
            )));
            params.put("mode", "payment");
            params.put("success_url", "https://ваш_сайт.ком/success");
            params.put("cancel_url", "https://ваш_сайт.ком/cancel");

            Session session = Session.create(params);
            return session;
        } catch (Exception e) {
            e.printStackTrace(); // Обработка ошибок
            return null;
        }
    }
}
