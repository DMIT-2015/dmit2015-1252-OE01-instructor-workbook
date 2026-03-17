package dmit2015.resource;

import common.validation.JavaBeanValidator;
import dmit2015.entity.Region;
import dmit2015.dto.RegionDto;
import dmit2015.mapper.RegionMapper;
import dmit2015.repository.RegionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.util.stream.Collectors;

/**
 * This Jakarta RESTful Web Services root resource class provides common REST API endpoints to
 * perform CRUD operations on the DTO (Data Transfer Object) for a Jakarta Persistence entity.
 */
@ApplicationScoped
@Path("Regions")                // All methods in this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)
// All methods in this class expects method parameters to contain data in JSON format
@Produces(MediaType.APPLICATION_JSON)    // All methods in this class returns data in JSON format
public class RegionDtoResource {

    @Inject
    private RegionRepository regionRepository;

    @GET    // This method only accepts HTTP GET requests.
    public Response findAllRegionsRegions() {
        return Response.ok(
                regionRepository
                        .findAll()
                        .stream()
                        .map(RegionMapper.INSTANCE::toDto)
                        .collect(Collectors.toList())
        ).build();
    }

    @Path("{id}")
    @GET    // This method only accepts HTTP GET requests.
    public Response findRegionByIdRegionById(@PathParam("id") Long id) {
        Region existingRegion = regionRepository.findById(id).orElseThrow(NotFoundException::new);

        RegionDto dto = RegionMapper.INSTANCE.toDto(existingRegion);

        return Response.ok(dto).build();
    }

    @POST    // This method only accepts HTTP POST requests.
    public Response createRegionRegion(RegionDto dto, @Context UriInfo uriInfo) {
        Region newRegion = RegionMapper.INSTANCE.toEntity(dto);

        String errorMessage = JavaBeanValidator.validateBean(newRegion);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        try {
            // Persist the new Region into the database
            regionRepository.add(newRegion);
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // uriInfo is injected via @Context parameter to this method
        URI location = UriBuilder
                .fromPath(uriInfo.getPath())
                .path("{id}")
                .build(newRegion.getId());

        // Set the location path of the new entity with its identifier
        // Returns an HTTP status of "201 Created" if the Region was created.
        return Response
                .created(location)
                .build();
    }

    @PUT            // This method only accepts HTTP PUT requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response updateRegionRegion(@PathParam("id") Long id, RegionDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        Region existingRegion = regionRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        Region updatedRegion = RegionMapper.INSTANCE.toEntity(dto);

        String errorMessage = JavaBeanValidator.validateBean(updatedRegion);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        existingRegion.setRegionName(updatedRegion.getRegionName());

        try {
            regionRepository.update(existingRegion);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("The data you are trying to update has changed since your last read request.")
                    .build();
        } catch (Exception ex) {
            // Return an HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "200 OK" and include in the body of the response the object that was updated
        RegionDto updatedDto = RegionMapper.INSTANCE.toDto(existingRegion);
        return Response.ok(updatedDto).build();
    }

    @DELETE            // This method only accepts HTTP DELETE requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response deleteRegionRegion(@PathParam("id") Long id) {

        Region existingRegion = regionRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        try {
            regionRepository.delete(existingRegion);    // Removes the Region from being persisted
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response
                    .serverError()
                    .encoding(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "204 No Content" to indicate the resource was deleted
        return Response.noContent().build();

    }

}