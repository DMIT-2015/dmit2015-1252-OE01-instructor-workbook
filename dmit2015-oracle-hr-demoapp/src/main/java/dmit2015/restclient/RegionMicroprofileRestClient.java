package dmit2015.restclient;

import dmit2015.dto.RegionDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Optional;

@RequestScoped
@RegisterRestClient(baseUri = "http://localhost:8090/restapi/Regions")
public interface RegionMicroprofileRestClient {

    @POST
    Response create(RegionDto dto);

    @GET
    List<RegionDto> findAll();

    @GET
    @Path("/{id}")
    Optional<RegionDto> findById(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    RegionDto update(@PathParam("id")Long id, RegionDto dto);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") Long id);
}
