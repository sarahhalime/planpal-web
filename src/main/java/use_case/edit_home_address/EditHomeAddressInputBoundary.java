package use_case.edit_home_address;

public interface EditHomeAddressInputBoundary {
    /**
     * Execute the edit home address Use Case.
     * @param editHomeAddressInputData the input data for this use case
     */
    void execute(EditHomeAddressInputData editHomeAddressInputData);
}
