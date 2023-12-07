package org.fon.models.connector;


import javafx.beans.value.ChangeListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class BConnector {
    @FunctionalInterface
    public interface BSlot0 {
        void apply();
    }
    @FunctionalInterface
    public interface BSlot1<A1> {
        void apply(A1 arg1);
    }

    @FunctionalInterface
    public interface BSlot2<A1, A2> {
        void apply(A1 arg1, A2 arg2);
    }

    @FunctionalInterface
    public interface BSlot3<A1, A2, A3> {
        void apply(A1 arg1, A2 arg2, A3 arg3);
    }

    @FunctionalInterface
    public interface BSlot4<A1, A2, A3, A4> {
        void apply(A1 arg1, A2 arg2, A3 arg3, A4 arg4);
    }

    @FunctionalInterface
    public interface BSlot5<A1, A2, A3, A4, A5> {
        void apply(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
    }
    
    
    //----------------------------------------------------------------------/
    public static Object connect(BSignal signalToConnect, BSlot0 slotToConnect)
    {
        return createListener(signalToConnect, slotToConnect);
    }

    public static <A1> Object connect(BSignal signalToConnect, BSlot1<A1> slotToConnect)
    {
        return createListener(signalToConnect, slotToConnect);
    }

    public static <A1, A2> Object connect(BSignal signalToConnect, BSlot2<A1, A2> slotToConnect)
    {
        return createListener(signalToConnect, slotToConnect);
    }

    public static <A1, A2, A3> Object connect(BSignal signalToConnect, BSlot3<A1, A2, A3> slotToConnect)
    {
        return createListener(signalToConnect, slotToConnect);
    }

    public static <A1, A2, A3, A4> Object connect(BSignal signalToConnect, BSlot4<A1, A2, A3, A4> slotToConnect)
    {
        return createListener(signalToConnect, slotToConnect);
    }

    public static <A1, A2, A3, A4, A5> Object connect(BSignal signalToConnect, BSlot5<A1, A2, A3, A4, A5> slotToConnect)
    {
        return createListener(signalToConnect, slotToConnect);
    }

    public static boolean disconnect(BSignal signalToConnect, Object connection)
    {
        return removeListener(signalToConnect, connection);
    }

    public static boolean disconnect(BSignal signalToConnect, BSlot0 slotToConnect)
    {
        return removeListener(signalToConnect, slotToConnect);
    }

    public static <A1> boolean disconnect(BSignal signalToConnect, BSlot1<A1> slotToConnect)
    {
        return removeListener(signalToConnect, slotToConnect);
    }

    public static <A1, A2> boolean disconnect(BSignal signalToConnect, BSlot2<A1, A2> slotToConnect)
    {
        return removeListener(signalToConnect, slotToConnect);
    }

    public static <A1, A2, A3> boolean disconnect(BSignal signalToConnect, BSlot3<A1, A2, A3> slotToConnect)
    {
        return removeListener(signalToConnect, slotToConnect);
    }

    public static <A1, A2, A3, A4> boolean disconnect(BSignal signalToConnect, BSlot4<A1, A2, A3, A4> slotToConnect)
    {
        return removeListener(signalToConnect, slotToConnect);
    }

    public static <A1, A2, A3, A4, A5> boolean disconnect(BSignal signalToConnect, BSlot5<A1, A2, A3, A4, A5> slotToConnect)
    {
        return removeListener(signalToConnect, slotToConnect);
    }


    //-------------------------------------------------------//

    private static final HashMap<Object, ChangeListener<BSignal.EmittedArgumentContainer>> staticConnectionHashMap = new HashMap<>();

    private static Object createListener(BSignal signalToConnect, Object slotObject)
    {
        Class<?> slotClass = slotObject.getClass();

        ChangeListener<BSignal.EmittedArgumentContainer> connection = (obs, oldVal, newVal) -> {
            try {
                Method method = slotClass.getDeclaredMethods()[0];
                method.setAccessible(true);
                method.invoke(slotObject, newVal.getArguments());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        };

        staticConnectionHashMap.put(slotObject, connection);
        signalToConnect.signalProducer.addListener(connection);

        return slotObject;
    }

    private static boolean removeListener(BSignal signalToConnect, Object slotObject)
    {
        ChangeListener<BSignal.EmittedArgumentContainer> connection = staticConnectionHashMap.remove(slotObject);
        if (connection == null) {
            System.out.println("BConnector.removeListener connection not found for provided function object");
            return false;
        }

        signalToConnect.signalProducer.removeListener(connection);
        return true;
    }

}
