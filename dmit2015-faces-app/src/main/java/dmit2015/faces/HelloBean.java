package dmit2015.faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

@Named//("helloBean")
@RequestScoped
public class HelloBean {

    @Getter @Setter
    private String userInput = "Server Faces";

    public String getMessage() {
        return "Hello, " + userInput;
    }

    public void submit() {

    }
}
