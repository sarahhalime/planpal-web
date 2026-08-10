package use_case.edit_home_address;

public interface EditHomeAddressOutputBoundary {

    /**
     * Prepares the success view for the edit home address Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(EditHomeAddressOutputData outputData);

    /**
     * Prepares the failure view for the edit home address Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailureView(String errorMessage);
}
