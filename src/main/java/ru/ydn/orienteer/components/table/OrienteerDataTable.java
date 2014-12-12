package ru.ydn.orienteer.components.table;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.orienteer.components.commands.Command;
import ru.ydn.orienteer.components.properties.AbstractMetaPanel;
import ru.ydn.orienteer.components.properties.IMetaContext;

public class OrienteerDataTable<T, S> extends DataTable<T, S>
{
	
	public static class MetaContextItem<T, C> extends Item<T> implements IMetaContext<C>
	{

		public MetaContextItem(String id, int index, IModel<T> model)
		{
			super(id, index, model);
		}

		@Override
		public MarkupContainer getContextComponent() {
			return this;
		}

		@Override
		public <K extends AbstractMetaPanel<?, C, ?>> K getMetaComponent(
				C critery) {
			return AbstractMetaPanel.getMetaComponent(this, critery);
		}
		
	}
	private static final long serialVersionUID = 1L;
	protected DataTableCommandsToolbar<T> commandsToolbar;
	protected HeadersToolbar<S> headersToolbar;
	protected NoRecordsToolbar noRecordsToolbar;
	
	private IModel<String> captionModel;
	
	public OrienteerDataTable(String id, List<? extends IColumn<T, S>> columns,
			ISortableDataProvider<T, S> dataProvider, int rowsPerPage)
	{
		super(id, columns, dataProvider, rowsPerPage);
		addTopToolbar(commandsToolbar=new DataTableCommandsToolbar<T>(this));
		addTopToolbar(headersToolbar =new HeadersToolbar<S>(this, dataProvider));
		addBottomToolbar(new NavigationToolbar(this));
		addBottomToolbar(noRecordsToolbar = new NoRecordsToolbar(this));
		setOutputMarkupPlaceholderTag(true);
	}

	public DataTableCommandsToolbar<T> getCommandsToolbar() {
		return commandsToolbar;
	}
	
	public HeadersToolbar<S> getHeadersToolbar() {
		return headersToolbar;
	}
	
	public NoRecordsToolbar getNoRecordsToolbar()
	{
		return noRecordsToolbar;
	}

	public OrienteerDataTable<T, S> addCommand(Command<T> command)
	{
		commandsToolbar.add(command);
		return this;
	}
	
	@Override
	public void onEvent(IEvent<?> event) {
		if(event.getPayload() instanceof AjaxRequestTarget && Broadcast.BUBBLE.equals(event.getType()))
		{
			AjaxRequestTarget target = ((AjaxRequestTarget)event.getPayload());
			target.add(this);
			onAjaxUpdate(target);
			event.stop();
		}
	}
	
	public void onAjaxUpdate(AjaxRequestTarget target)
	{
	}

	@Override
	public IModel<String> getCaptionModel() {
		if(captionModel==null)
		{
			captionModel = Model.of("");
		}
		return captionModel;
	}
	
	public OrienteerDataTable<T, S> setCaptionModel(IModel<String> captionModel) {
		get("caption").setDefaultModel(captionModel);
		this.captionModel = captionModel;
		return this;
	}

	@Override
	public void detachModels() {
		super.detachModels();
		if(captionModel!=null) captionModel.detach();
	}

	@Override
	protected Item<T> newRowItem(final String id, final int index, final IModel<T> model)
	{
		return new MetaContextItem<T, Object>(id, index, model);
	}
	
}
