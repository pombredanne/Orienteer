package ru.ydn.orienteer.components.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.BootstrapSize;
import ru.ydn.orienteer.components.BootstrapType;
import ru.ydn.orienteer.components.FAIconType;
import ru.ydn.orienteer.components.commands.AjaxFormCommand;
import ru.ydn.orienteer.components.commands.SelectODocumentCommand;
import ru.ydn.orienteer.components.table.OEntityColumn;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;
import ru.ydn.orienteer.services.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LinksCollectionEditPanel<T extends OIdentifiable, M extends Collection<T>> extends GenericPanel<M>
{
	private static final long serialVersionUID = 1L;
	
	@Inject
	private IOClassIntrospector oClassIntrospector;

	public LinksCollectionEditPanel(String id, final IModel<ODocument> documentModel, OProperty property) {
		super(id, new DynamicPropertyValueModel<M>(documentModel, new OPropertyModel(property)));
		
		OQueryDataProvider<ODocument> provider = oClassIntrospector.prepareDataProviderForProperty(property, documentModel);
		final String propertyName = property.getName();
		
		List<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument,String>>();
		columns.add(new OEntityColumn<ODocument>(property.getLinkedClass()));
		columns.add(new AbstractColumn<ODocument, String>(null) {

			@Override
			public void populateItem(Item<ICellPopulator<ODocument>> cellItem,
					String componentId, final IModel<ODocument> rowModel) {
				
				cellItem.add(new AjaxFormCommand<Object>(componentId, new ResourceModel("command.release"))
						{
							{
								setBootstrapType(BootstrapType.WARNING);
								setBootstrapSize(BootstrapSize.EXTRA_SMALL);
							}

							@Override
							public void onClick(AjaxRequestTarget target) {
								ODocument doc = documentModel.getObject();
								Collection<ODocument> values = doc.field(propertyName);
								if(values!=null)
								{
									values.remove(rowModel.getObject());
								}
								doc.save();
								send(LinksCollectionEditPanel.this, Broadcast.BUBBLE, target);
							}
					
						});
			}
		});
		
		OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("links", columns, provider, 10);
		table.getHeadersToolbar().setVisibilityAllowed(false);
		table.getNoRecordsToolbar().setVisibilityAllowed(false);
		table.addCommand(new SelectODocumentCommand(table, documentModel, new OPropertyModel(property))
				.setBootstrapSize(BootstrapSize.EXTRA_SMALL)
				.setIcon((String)null));
		add(table);
	}
}
