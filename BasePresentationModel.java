package nppltt.ppsmobileclient.ViewModels;

import android.widget.TextView;

import org.robobinding.presentationmodel.AbstractPresentationModel;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;
import org.robobinding.property.PropertyChangeListener;
import org.robobinding.property.PropertyChangeListeners;
import org.robobinding.property.PropertyChangeSupport;
import org.robobinding.viewattribute.property.AbstractBindingProperty;
import org.robobinding.viewattribute.property.TwoWayBindingProperty;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import nppltt.ppsmobileclient.Infrastructure.Event;

public abstract class BasePresentationModel extends AbstractPresentationModel {

    /**
     * Show validation message (errorMessage) in all user controls, that binds into field (fieldName)
     * @param fieldName field of presentation model
     * @param errorMessage message on error
     */
    public void setError (String fieldName, String errorMessage)
    {
        try
        {
            Field presentationModelChangeSupportField = AbstractPresentationModel.class.getDeclaredField("presentationModelChangeSupport");
            presentationModelChangeSupportField.setAccessible(true);
            PresentationModelChangeSupport presentationModelChangeSupport = (PresentationModelChangeSupport)presentationModelChangeSupportField.get(this);

            Field propertyChangeSupportField = PresentationModelChangeSupport.class.getDeclaredField("propertyChangeSupport");
            propertyChangeSupportField.setAccessible(true);
            PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)propertyChangeSupportField.get(presentationModelChangeSupport);

            Field propertyChangeListenerMapField = PropertyChangeSupport.class.getDeclaredField("propertyChangeListenerMap");
            propertyChangeListenerMapField.setAccessible(true);
            Map<String, PropertyChangeListeners> propertyChangeListenerMap = (Map<String, PropertyChangeListeners>)propertyChangeListenerMapField.get(propertyChangeSupport);

            if (propertyChangeListenerMap.containsKey(fieldName))
            {
                PropertyChangeListeners propertyChangeListeners = propertyChangeListenerMap.get(fieldName);

                Field listenersField = PropertyChangeListeners.class.getDeclaredField("listeners");
                listenersField.setAccessible(true);

                Set<PropertyChangeListener> listeners = (Set<PropertyChangeListener>)listenersField.get(propertyChangeListeners);

                if(listeners.toArray().length > 0)
                {
                    for (PropertyChangeListener listener : listeners) {
                        Field valueModelField = listener.getClass().getDeclaredFields()[0];
                        valueModelField.setAccessible(true);
                        TwoWayBindingProperty twoWayBindingProperty = (TwoWayBindingProperty)valueModelField.get(listeners.toArray()[0]);

                        Field viewField = AbstractBindingProperty.class.getDeclaredField("view");
                        viewField.setAccessible(true);
                        Object obj = viewField.get(twoWayBindingProperty);

                        TextView textView = obj instanceof TextView ? ((TextView)viewField.get(twoWayBindingProperty)) : null;
                        if(textView != null)
                        {
                            textView.setError(errorMessage);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
