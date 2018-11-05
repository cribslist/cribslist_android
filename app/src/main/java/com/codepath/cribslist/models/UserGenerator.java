package com.codepath.cribslist.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UserGenerator {
    private static final Random RANDOM = new Random();
    public static final ArrayList<String> randomNamesMen = new ArrayList<String>(Arrays.asList("Sukwon", "Morgan", "Roger", "Brian", "Asher", "Tomo", "Jon", "Droan", "Yaw", "Alex", "Mike","Matt", "Garland"));
    public static final ArrayList<String> randomNamesWomen = new ArrayList<String>(Arrays.asList("Marina", "Lauren", "Linda", "Cerina", "Nik", "Mansi", "Lia", "Clara", "Xin", "Vidya", "Terra"));
    public static final ArrayList<String> randomLocations = new ArrayList<String>(Arrays.asList("Cow hollow", "NOPA", "Panhandle", "Sunset", "Ocean Beach", "Richmond", "North Beach", "West Portal", "Civic Center", "Van Ness", "Nob Hill", "Mission", "Pac Heights"));
    public static final ArrayList<String> randomEmails = new ArrayList<String>(Arrays.asList("@gmail.com", "@hotmail.com", "@yahoo.com", "@aol.com", "@cox.net", "@hearsaycorp.com", "@hearsaylabs.com", "@comcast.net", "@cribslist.com"));
    public static final ArrayList<String> randomAdj = new ArrayList<String>(Arrays.asList(
            "full", "whole", "certain", "human", "major",
            "military",
            "bad",
            "social",
            "dead",
            "true",
            "economic",
            "open",
            "early",
            "free",
            "national",
            "strong",
            "hard",
            "special",
            "clear",
            "local",
            "private",
            "wrong",
            "late",
            "short",
            "poor",
            "recent",
            "dark",
            "fine",
            "foreign",
            "ready",
            "red",
            "cold",
            "low",
            "heavy",
            "serious",
            "single",
            "personal",
            "difficult",
            "left",
            "blue",
            "federal",
            "necessary",
            "general",
            "easy",
            "likely",
            "beautiful",
            "happy",
            "past",
            "hot",
            "close",
            "common",
            "afraid",
            "simple",
            "natural",
            "main",
            "various",
            "available",
            "nice",
            "present",
            "final",
            "sorry",
            "entire",
            "current",
            "similar",
            "deep",
            "huge",
            "rich",
            "nuclear",
            "empty",
            "strange",
            "quiet",
            "front",
            "wide",
            "modern",
            "concerned",
            "green",
            "very",
            "alone",
            "particular",
            "bright",
            "supposed",
            "basic",
            "medical",
            "aware",
            "total",
            "financial",
            "legal",
            "original",
            "international",
            "soft",
            "alive",
            "interested",
            "tall",
            "warm",
            "popular",
            "tiny",
            "top",
            "normal",
            "powerful",
            "silent",
            "religious",
            "impossible",
            "quick",
            "safe"));
    private static final List<Gender> VALUES =
            Collections.unmodifiableList(Arrays.asList(Gender.values()));
    private static final int SIZE = VALUES.size();
    public static final String imageUrlMen = "https://randomuser.me/api/portraits/med/men/";
    public static final String imageUrlWomen = "https://randomuser.me/api/portraits/med/women/";
    public static String getMaleName(){
        int index = RANDOM.nextInt(randomNamesMen.size());
        return randomNamesMen.get(index);
    }
    public static String getFemaleName(){
        int index = RANDOM.nextInt(randomNamesWomen.size());
        return randomNamesWomen.get(index);
    }

    public static String getAdj(){
        int index = RANDOM.nextInt(randomAdj.size());
        return randomAdj.get(index);
    }

    public static User getRandomUser(long uid){
        User user = new User(uid);
        Gender gender = getGender();
        String name = getName(gender);
        user.setName(name);
        user.setUserPhotoURL(getImageUrl(gender));
        user.setEmail(getEmail(name));
        user.setLocation(getLocation());
        return user;
    }

    public static String getName (Gender g){
        return g == Gender.MALE ? getMaleName() : getFemaleName();
    }
    public static String getImageUrl(Gender g){
        String value = String.valueOf(Math.round(Math.random() * 60));
        String baseUrl = Gender.MALE == g ? imageUrlMen : imageUrlWomen;
        return baseUrl + value + ".jpg";
    }
    public static String getLocation(){
        int index = RANDOM.nextInt(randomLocations.size());
        return randomLocations.get(index);
    }


    public static Gender getGender(){
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public static String getEmail(String name){
        int emailIndex = RANDOM.nextInt(randomEmails.size());
        return getAdj() + name + randomEmails.get(emailIndex);
    }
}

