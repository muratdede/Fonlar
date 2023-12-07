package org.fon.models.connector;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class BSignal {
    private final String emitOwner;

    public BSignal() {
        this(null);
    }

    public BSignal(Object parent) {
        if (parent != null)
            emitOwner = parent.getClass().getName();
        else
            emitOwner = null;
    }

    static class EmittedArgumentContainer {
        private final Object[] arguments;

        public EmittedArgumentContainer(Object... args) {
            arguments = args;
        }

        public Object[] getArguments() {
            return arguments;
        }
    }

    final ObjectProperty<EmittedArgumentContainer> signalProducer = new SimpleObjectProperty<>(this, "signalProducer");

    public void emit(Object... args)
    {
        if (!checkPrivilege()) {
            System.err.println("Illegal Access!!");
            return;
        }

        signalProducer.set(new EmittedArgumentContainer(args));
    }

    private boolean checkPrivilege() {
        if (emitOwner == null)
            return true;

        return emitOwner.equals(getCallerClass());
    }

    private String getCallerClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 5)
            return null;

        System.out.println(stackTrace[4].getClass().getName());
        return stackTrace[4].getClassName();
    }
}
