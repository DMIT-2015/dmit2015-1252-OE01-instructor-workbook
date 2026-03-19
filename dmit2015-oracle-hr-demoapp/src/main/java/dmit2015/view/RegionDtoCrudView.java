package dmit2015.view;

import dmit2015.dto.RegionDto;
import dmit2015.service.RegionDtoService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;
import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * This Jakarta Faces backing bean class contains the data and event handlers
 * to perform CRUD operations using a PrimeFaces DataTable configured to perform CRUD.
 */
@Named("currentRegionDtoCrudView")
@ViewScoped // create this object for one HTTP request and keep in memory if the next is for the same page
public class RegionDtoCrudView implements Serializable {

    @Inject
    @Named("currentMicroprofileRestClientRegionService")
    private RegionDtoService regionDtoService;

    /**
     * The selected RegionDto instance to create, edit, update or delete.
     */
    @Getter
    @Setter
    private RegionDto selectedRegionDto;

    /**
     * The unique name of the selected RegionDto instance.
     */
    @Getter
    @Setter
    private Long selectedId;

    /**
     * The list of RegionDto objects fetched from the data source
     */
    @Getter
    private List<RegionDto> regionDtos;

    /**
     * Fetch all RegionDto from the data source.
     * <p>
     * If FacesContext message sent from init() method annotated with @PostConstruct in the Faces backing bean class are not shown on page:
     * 1) Remove the @PostConstruct annotation from the Faces backing bean class
     * 2) Add metadata tag shown below to the page to execute the init() method
     * <f:metadata>
     * <f:viewParam name="dummy" />
     * <f:event type="postInvokeAction" listener="#{currentBeanView.init}" />
     * </f:metadata>
     */
    @PostConstruct
    public void init() {
        try {
            regionDtos = regionDtoService.getAllRegionDtos();
        } catch (Exception e) {
            Messages.addGlobalError("Error getting regionDtos %s", e.getMessage());
        }
    }

    /**
     * Event handler for the New button on the Faces crud page.
     * Create a new selected RegionDto instance to enter data for.
     */
    public void onOpenNew() {
        selectedRegionDto = new RegionDto();
        selectedId = null;
    }


    /**
     * Event handler for Save button to create or update data.
     */
    public void onSave() {
        try {

            // If selectedId is null then create new data otherwise update current data
            if (selectedId == null) {
                RegionDto createdRegionDto = regionDtoService.createRegionDto(selectedRegionDto);

                // Send a Faces info message that create was successful
                Messages.addGlobalInfo("Create was successful. {0}", createdRegionDto.getId());
                // Reset the selected instance to null
                selectedRegionDto = null;

            } else {
                regionDtoService.updateRegionDto(selectedRegionDto);

                Messages.addGlobalInfo("Update was successful");

            }

            // Fetch a list of objects from the data source
            regionDtos = regionDtoService.getAllRegionDtos();
            PrimeFaces.current().ajax().update("dialogs:messages", "form:dt-RegionDtos");

            // Hide the PrimeFaces dialog
            PrimeFaces.current().executeScript("PF('manageRegionDtoDialog').hide()");
        } catch (RuntimeException ex) { // handle application generated exceptions
            Messages.addGlobalError(ex.getMessage());
        } catch (Exception ex) {    // handle system generated exceptions
            Messages.addGlobalError("Save not successful.");
            handleException(ex);
        }

    }

    /**
     * Event handler for Delete to delete selected data.
     */
    public void onDelete() {
        try {
            // Get the unique name of the Json object to delete
            selectedId = selectedRegionDto.getId();
            regionDtoService.deleteRegionDtoById(selectedId);
            Messages.addGlobalInfo("Delete was successful for id of {0}", selectedId);
            // Fetch new data from the data source
            regionDtos = regionDtoService.getAllRegionDtos();

            PrimeFaces.current().ajax().update("dialogs:messages", "form:dt-RegionDtos");
        } catch (RuntimeException ex) { // handle application generated exceptions
            Messages.addGlobalError(ex.getMessage());
        } catch (Exception ex) {    // handle system generated exceptions
            Messages.addGlobalError("Delete not successful.");
            handleException(ex);
        }

    }

    /**
     * This method is used to handle exceptions and display root cause to user.
     *
     * @param ex The Exception to handle.
     */
    protected void handleException(Exception ex) {
        StringBuilder details = new StringBuilder();
        Throwable causes = ex;
        while (causes.getCause() != null) {
            details.append(ex.getMessage());
            details.append("    Caused by:");
            details.append(causes.getCause().getMessage());
            causes = causes.getCause();
        }
        Messages.create(ex.getMessage()).detail(details.toString()).error().add("errors");
    }

}
