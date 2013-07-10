package pl.bedkowski.code.gwtvalidation.client.datasource;

import com.google.gwt.user.client.rpc.AsyncCallback;

import pl.bedkowski.code.gwtvalidation.client.DTO;

public interface AsyncCreateUpdate<T extends DTO> {

	public void create(T dto, AsyncCallback<Void> callback);

	public void update(T dto, AsyncCallback<Void> callback);

}
