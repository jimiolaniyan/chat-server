import java.rmi.RemoteException;

public class ClientActionsImpl implements ClientActions {

    String name;

    public ClientActionsImpl(String n) {
        name = n;
    }

    @Override
    public void getMessage(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

}
