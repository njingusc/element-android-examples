package com.element.example;

class DemoAppUser {

    final String name;
    final String elementId;

    DemoAppUser(String name, String elementId) {
        this.name = name;
        this.elementId = elementId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof DemoAppUser) {
            DemoAppUser that = (DemoAppUser) o;
            return this.elementId.equals(that.elementId);
        } else if (o instanceof String) {
            return elementId.equals(o);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return elementId.hashCode();
    }
}
