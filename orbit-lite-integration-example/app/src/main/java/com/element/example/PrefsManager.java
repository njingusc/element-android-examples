package com.element.example;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class PrefsManager {

    private final SharedPreferences sharedPrefs;

    private final Gson gson;

    PrefsManager(Context context) {
        this.gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // **************************************************
    // *** Generic
    // **************************************************

    void putValue(String key, List<String> list) {
        SharedPreferences.Editor edit = sharedPrefs.edit();
        Set<String> set = Sets.newLinkedHashSet(list);
        edit.putStringSet(key, set);
        edit.apply();
    }

    // **************************************************
    // *** String
    // **************************************************

    String getStringValue(String key) {
        return getStringValue(key, "");
    }

    String getStringValue(String key, String defaultStr) {
        return sharedPrefs.getString(key, defaultStr);
    }

    // **************************************************
    // *** Set
    // **************************************************

    Set<String> getStringSet(String key) {
        return getStringSet(key, new LinkedHashSet<String>());
    }

    Set<String> getStringSet(String key, Set<String> defaultSet) {
        return sharedPrefs.getStringSet(key, defaultSet);
    }

    // **************************************************
    // *** Generics
    // **************************************************

    <T> List<T> getList(String key, final Class<T> clazz) {
        Set<String> set = getStringSet(key);
        if (set.size() == 0) {
            List<T> list = new ArrayList<>();
            return list;
        }

        List<String> list = new ArrayList<>(set);
        Function<String, T> function = new Function<String, T>() {
            @Override
            public T apply(String s) {
                return gson.fromJson(s, clazz);
            }
        };
        List<T> l = Lists.transform(list, function);
        return new ArrayList<>(l);
    }

    <T> void saveList(String key, List<T> list) {
        Function<T, String> function = new Function<T, String>() {
            @Override
            public String apply(T t) {
                return gson.toJson(t);
            }
        };
        List<String> converted = Lists.transform(list, function);
        putValue(key, converted);
    }

    // **************************************************
    // *** boolean
    // **************************************************

    boolean getBooleanValue(String key) {
        return getBooleanValue(key, false);
    }

    boolean getBooleanValue(String key, boolean defaultVal) {
        return sharedPrefs.getBoolean(key, defaultVal);
    }

    // **************************************************
    // *** long
    // **************************************************

    long getLongValue(String key) {
        return getLongValue(key, 0);
    }

    long getLongValue(String key, long defaultVal) {
        return sharedPrefs.getLong(key, defaultVal);
    }

    // **************************************************
    // *** float
    // **************************************************

    float getFloatValue(String key) {
        return getFloatValue(key, 0);
    }

    float getFloatValue(String key, float defaultVal) {
        return sharedPrefs.getFloat(key, defaultVal);
    }
}
