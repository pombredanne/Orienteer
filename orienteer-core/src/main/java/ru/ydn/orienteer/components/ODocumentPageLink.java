package ru.ydn.orienteer.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.model.DocumentNameModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;

public class ODocumentPageLink<M extends OIdentifiable> extends BookmarkablePageLink<M>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean propogateDisplayMode = true;
	private IModel<DisplayMode> displayModeModel;
	public ODocumentPageLink(String id, IModel<M> docModel, PageParameters parameters)
	{
		this(id, docModel, DisplayMode.VIEW, parameters);
	}
	
	public ODocumentPageLink(String id, IModel<M> docModel)
	{
		this(id, docModel, DisplayMode.VIEW);
	}
	
	public ODocumentPageLink(String id, IModel<M> docModel, DisplayMode mode, PageParameters parameters)
	{
		this(id, docModel, mode.getDefaultPageClass(), mode.asModel(), parameters);
	}
	
	public ODocumentPageLink(String id, IModel<M> docModel, DisplayMode mode)
	{
		this(id, docModel, mode.getDefaultPageClass(), mode.asModel());
	}
	public <C extends Page> ODocumentPageLink(String id, IModel<M> docModel, Class<C> pageClass, 
			IModel<DisplayMode> displayModeModel, PageParameters parameters) {
		super(id, pageClass, parameters);
		setModel(docModel);
		this.displayModeModel = displayModeModel;
	}

	public <C extends Page> ODocumentPageLink(String id, IModel<M> docModel, Class<C> pageClass,
			IModel<DisplayMode> displayModeModel) {
		super(id, pageClass);
		setModel(docModel);
		this.displayModeModel = displayModeModel;
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getModelObject()!=null);
	}

	public ODocumentPageLink<M> setDocumentNameAsBody(boolean docNameAsBody)
	{
		setBody(docNameAsBody?new DocumentNameModel(getModel()):null);
		return this;
	}
	
	public ODocumentPageLink<M> setPropogateDisplayMode(boolean propogateDisplayMode)
	{
		this.propogateDisplayMode = propogateDisplayMode;
		return this;
	}
	
	@Override
	public PageParameters getPageParameters() {
		PageParameters pageParameters = super.getPageParameters();
		pageParameters.add("rid", buitifyRid(getModelObject()));
		if(propogateDisplayMode)
		{
			pageParameters.add("mode", displayModeModel.getObject().getName());
		}
		return pageParameters;
	}
	
	public String buitifyRid(OIdentifiable identifiable)
	{
		if(identifiable==null) return "";
		String ret = identifiable.getIdentity().toString();
		return ret.charAt(0)==ORID.PREFIX?ret.substring(1):ret;
	}

}
