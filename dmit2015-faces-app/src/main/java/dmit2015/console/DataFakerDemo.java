package dmit2015.console;

import net.datafaker.Faker;

import java.util.Locale;

public class DataFakerDemo {

    static void main() {
        var faker = new Faker();

        var arabicFaker = new Faker(new Locale.Builder().setLanguage("ar").build());

        var chineseSimplifiedFaker = new Faker(Locale.SIMPLIFIED_CHINESE);

        String name = chineseSimplifiedFaker
                .name().fullName();

        String address = arabicFaker.address().fullAddress();

        String email = faker.internet().emailAddress();

        String phone = faker.phoneNumber().phoneNumberInternational();

        System.out.printf("Name: %s\n", name);

        System.out.printf("Address: %s\n", address);

        System.out.printf("Email: %s\n", email);

        System.out.printf("Phone: %s\n", phone);
    }
}
