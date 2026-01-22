package dmit2015.faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import net.datafaker.Faker;
import org.omnifaces.util.Messages;


@Named("hello")
@RequestScoped
public class HelloBean {

    @NotBlank(message = "User input is required.")
    private String userInput;

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getMessage() {
        return "Hello, " + userInput;
    }

    public String submit() {

        // Create a Faker instance for generating realistic fake data
        var faker = new Faker();
        var pokemonName = faker.pokemon().name();
        var pokemonMove = faker.pokemon().move();
        Messages.addGlobalInfo("Hi {0}, you got a {1} pokemon that likes to {2}",
                userInput, pokemonName, pokemonMove);
        return null; // or navigation outcome
    }
}