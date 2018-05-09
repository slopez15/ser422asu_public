package ab.ser422.client;

import ab.ser422.AddressBookServiceStub;

public class abClient {
    public static void main(String[] args) {
	try {
	    AddressBookServiceStub abss = new AddressBookServiceStub("http://localhost:8080/axis2/services/AddressBookService");
	    // find any entries
	    AddressBookServiceStub.FindEntry abssReq = new AddressBookServiceStub.FindEntry();
	    abssReq.setArgs0(args[0]);
	    
	    AddressBookServiceStub.FindEntryResponse abssRes = abss.findEntry(abssReq);
	    
	    AddressBookServiceStub.Entry responseEntry = abssRes.get_return();
	    if (responseEntry == null) {
		System.out.println("No such address entry found");
	    } else {
		System.out.println("Name   :" + responseEntry.getName());
		System.out.println("Street :" + responseEntry.getStreet());
		System.out.println("City   :" + responseEntry.getCity());
		System.out.println("State  :" + responseEntry.getState());
		System.out.println("Postal Code :" + responseEntry.getPostalCode());
	    }

	} catch (Exception exc) {
	    exc.printStackTrace();
	}
	System.out.println("\nDONE");
    }
}
