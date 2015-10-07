# About Cupboard Tools
The goal of this project is to complement [Cupboard][1] with common and useful tools, without messing too much
with the core project, which is considered to be pretty stable.

Currently it consists of a [ContentProvider][2] and a [Helper for Uris][3] and some [converters][4].

## Using from Gradle
Cupboard tools is hosted on [Bintray][5] and jcenter. To use cupboard-tools, just add a depdendency to your `build.gradle`

```
compile 'nl.littlerobots:cupboard-tools:<version>'
```

# CupboardContentProvider

`CupboardContentProvider` serves as a base class to reduce a lot of boilerplate. It's built on the AOSP `SQLiteContentProvider`.
 The provider serves a couple of purposes:

 * Using `UriHelper` each Cupboard entity has it's own uri. A uri with an id appended denotes a specific entity, otherwise a collection of entities.
 * Every query returns a `Cursor` that will notify changes to that cursor. This will make a `CursorLoader` automatically reload
 * Easy to setup & extend

The most basic form of a `CupboardContentProvider` subclass looks like this:

    :::java
    public class MyProvider extends CupboardContentProvider {
        // The content provider authority is used for building Uri's for the provider
        public static final String AUTHORITY = BuildConfig.APPLICATION_ID+".provider";

        static {
            cupboard().register(Cheese.class);
            cupboard().register(Plateau.class);
        }

        public MyProvider() {
            super(AUTHORITY, 1);
        }
    }

This snippet will register the `Cheese` and `Plateau` entities with Cupboard when the provider is loaded.
The provider has a default `SQLiteOpenHelper` that will create or update tables for the registered entities.

To use the provider in your app, register it in your `AndroidManifest.xml`

    :::xml
    <provider
            android:exported="false"
            android:authorities="${applicationId}.provider"
            android:name=".provider.MyProvider"/>

Note that for convenience, `${applicationId}` is used for the package name, in the same way as `BuildConfig.APPLICATION_ID` is used
in the provider subclass.

Working with the content provider, leveraging `UriHelper`, looks like this:

    :::java
    public List<Cheese> queryCheeses() {
        // Create the helper. Could also be in a class field or injected by Dagger
        UriHelper helper = UriHelper.with(MyProvider.AUTHORITY);
        Uri cheeseUri = helper.getUri(Cheese.class);
        return cupboard().withContext(mContext).query(cheeseUri, Cheese.class).list();
    }

    public Cheese getCheeseById(long id) {
        UriHelper helper = UriHelper.with(MyProvider.AUTHORITY);
        Uri cheeseUri = helper.getUri(Cheese.class);
        return cupboard().withContext(mContext).get(ContentUris.withAppendedId(cheeseUri, id), Cheese.class);
    }

Note that is just "plain" Cupboard code, just that the `Uri` is retrieved from the `UriHelper`. There's nothing special to
the uri either, it can be queried with or without Cupboard.

# UriHelper

In the example above, we already used `UriHelper`. `UriHelper` maps entity classes to content provider uris. There are
a couple of variants to get a `UriHelper`:

    :::java
    // will get a helper for the specified uri and the default Cupboard instance
    UriHelper helper = UriHelper.with(MyProvider.AUTHORITY);
    // will get a helper for the specified uri and the default Cupboard instance,
    // but the paths will be prefixed with /my/prefix. This is useful if you want to map
    // the same entity class multiple times in the provider.
    UriHelper helper2 = UriHelper.with(MyProvider.AUTHORITY, "/my/prefix");
    // will get a helper that supports the entities registered with the given
    // Cupboard instance, e.g. not using the default cupboard() instance.
    UriHelper helper3 = UriHelper.with(myCupboardInstance).forAuthority(MyProvider.AUTHORITY);

Getting a uri for a class is as easy as

    :::java
    Uri cheeseUri = helper.getUri(Cheese.class);

### Matching Uri's

`UriHelper` makes it easy to match uri's based on entity classes. By default `CupboardContentProvider` will do this
for you, but if you do not want to use `CupboardContentProvider` or need custom matching you can use `UriHelper` like this:

    :::java
    // Check if the given uri matches the Cheese class and if this uri matches the collection of cheeses
    // in stead of a particular Cheese by id.
    if (mUriHelper.getMatchedClass(uri) == Cheese.class && mUriHelper.isCollection(uri)) {
        // handle this case
    }

To clarify: a uri can be classified as a _collection_ uri. This simply means that there's no id appended to the uri for a given entity.
Cupboard will automatically append a uri to the entity uri, if the entity has it's `_id` field set. By using `isCollection()` on the helper
we effectively check if this uri operates on an entity with it's id set or not.

In scenarios where entities can be modified from multiple sources it's often useful to have multiple `UriHelper`s. For example changes that
are coming from `SyncAdapter` operations might be treated differently compared to changes that a user is making.

# ListFieldConverter

By design, Cupboard does not deal with the relational mapping of objects. In the cases where you don't need the relational aspect,
but you do need to store a `List<>` of things with the entity, `ListFieldConverter` can be used to "embed" the collection in the Cupboard
entity.

The entity below is just a plain POJO:

    :::java
    public class Plateau {
        public Long _id;
        public List<Cheese> cheeses;
    }

By default, Cupboard will throw an error when you try to `register()` this entity, since it does not know how to handle the `List<Cheese>`
property. Cupboard however supports the concept of `FieldConverter`s which specify how a field type is converted to and from the
database. `ListFieldConverter` will use the [Gson][6] library to serialize and deserialize the list. Note that this has two consequences:

1. The `Cheese` class does not have to be an entity that is registered with Cupboard
1. The list is serialized, so it's not (easily) possible to query the implicit relation between a `Plateau` and its `Cheese`s

To tell Cupboard to use `ListFieldConverter` for all `List` types, you need to do two things:

1. Create a new `Cupboard` instance using `CupboardBuilder` and register the field converter
1. Set the created instance as the default instance returned by `cupboard()`


    
```
#!java
// register a ListFieldConverterFactory that will serialize any List<> to json in the database
// using Gson
CupboardFactory.setCupboard(new CupboardBuilder().
                                registerFieldConverterFactory(new ListFieldConverterFactory(new Gson())).build());
```

You can pass an instance of `Gson` to further tweak how the POJOs in the list are transformed by Gson.

# GsonFieldConverter

Sometimes it's convenient to embed any POJO (including Cupboard entities) if it's used as a field using Gson.
In that case `GsonFieldConverter` helps.

`GsonFieldConverter` is also registered using `CupboardBuilder`

    :::java
    CupboardFactory.setCupboard(new CupboardBuilder().
            registerFieldConverter(Cheese.class, new GsonFieldConverter<>(new Gson(), Cheese.class)).build());

Note that in this case `Cheese` can be either an entity registered with Cupboard, or just a plain POJO. Also in this case,
querying the 1:1 relation would not be possible, since the field is stored as Json in the database.

## Repository

The repository is hosted on [Bintray][5]. Make sure you add `jcenter()` to your Gradle build file!

# License

    Copyright 2015 Little Robots

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[1]: https://bitbucket.org/littlerobots/cupboard
[2]: https://bitbucket.org/littlerobots/cupboard-tools/src/a89d1c43864890ad3a6dfeb7824d940287c1cea8/tools/src/main/java/nl/littlerobots/cupboard/tools/provider/CupboardContentProvider.java?at=default
[3]: https://bitbucket.org/littlerobots/cupboard-tools/src/a89d1c43864890ad3a6dfeb7824d940287c1cea8/tools/src/main/java/nl/littlerobots/cupboard/tools/provider/UriHelper.java?at=default
[4]: https://bitbucket.org/littlerobots/cupboard-tools/src/a89d1c43864890ad3a6dfeb7824d940287c1cea8/tools/src/main/java/nl/littlerobots/cupboard/tools/convert/?at=default
[5]: https://bintray.com/littlerobots/android/cupboard-tools/view
[6]: https://github.com/google/gson