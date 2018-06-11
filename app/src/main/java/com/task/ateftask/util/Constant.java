package com.task.ateftask.util;

public interface Constant {

    interface FacebookPermissions {
        String publicProfilePermission = "public_profile";
        String emailPermission = "email";
    }

    interface Graph {
        String PICTURE = "picture";
        String DATA = "data";
        String URL = "url";
        String ID = "id";
        String EMAIL = "email";
        String NAME = "name";
        String FIELDS = "fields";
        String FIELDS_ATTRIBUTES = "picture.type(large),name,id,email";
    }

    interface Extras {
        String USER = "user";
    }

    interface Key{
        String API_KEY="AIzaSyDjLljzOO6nd13HGn-tNJ8OWP-mtJHwqwU";
    }

}
