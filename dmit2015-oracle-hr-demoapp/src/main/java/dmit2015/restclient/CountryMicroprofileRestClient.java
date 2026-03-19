package dmit2015.restclient;

import dmit2015.dto.CountryDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Optional;

@RequestScoped
@RegisterRestClient(baseUri = "http://localhost:8090/restapi/countries")
public interface CountryMicroprofileRestClient {

    @POST
    Response create(CountryDto dto);

    @GET
    List<CountryDto> findAll();

    @GET
    @Path("/{id}")
    Optional<CountryDto> findById(@PathParam("id") String id);

    @PUT
    @Path("/{id}")
    CountryDto update(@PathParam("id")String id, CountryDto dto);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id);
}