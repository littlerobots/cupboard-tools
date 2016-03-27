/*
 *    Copyright 2015 Little Robots
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package nl.littlerobots.cupboard.tools.provider;

import android.annotation.SuppressLint;
import android.content.UriMatcher;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.qbusict.cupboard.Cupboard;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Helper to create canonical {@link android.net.Uri}s for registered entities
 */
public class UriHelper {
    private final UriMatcher mMatcher;
    private final String mAuthority;
    private final List<Class<?>> mEntities = new ArrayList<Class<?>>(20);
    private final List<String> mPaths = new ArrayList<String>(20);
    private final Uri mBaseUri;
    private int mRuleCount = 0;


    private UriHelper(Uri baseUri, List<Class<?>> entities) {
        mAuthority = baseUri.getAuthority();
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mBaseUri = baseUri;
        addAll(entities);
    }

    /**
     * Create an {@link nl.littlerobots.cupboard.tools.provider.UriHelper.AuthorityBuilder} for the supplied {@link nl.qbusict.cupboard.Cupboard} instance
     *
     * @param cupboard the instance
     * @return the builder
     */
    public static AuthorityBuilder with(Cupboard cupboard) {
        return new AuthorityBuilder(cupboard.getRegisteredEntities());
    }

    /**
     * Create a helper for the supplied base uri. The instance returned by {@link nl.qbusict.cupboard.CupboardFactory#cupboard()} is used for
     * retrieving the registered entities
     *
     * @param baseUri the base uri
     * @return a new UriHelper
     */
    public static UriHelper with(Uri baseUri) {
        return new AuthorityBuilder(cupboard().getRegisteredEntities()).forBaseUri(baseUri);
    }

    /**
     * Create a helper for the supplied authority. The instance returned by {@link nl.qbusict.cupboard.CupboardFactory#cupboard()} is used for
     * retrieving the registered entities
     *
     * @param authority the authority
     * @return a new UriHelper
     */
    public static UriHelper with(String authority) {
        return new AuthorityBuilder(cupboard().getRegisteredEntities()).forAuthority(authority);
    }

    /**
     * Create a helper for the supplied authority and path. The instance returned by {@link nl.qbusict.cupboard.CupboardFactory#cupboard()} is used for
     * retrieving the registered entities
     *
     * @param authority the authority
     * @param path      the path prefix
     * @return a new UriHelper
     */
    public static UriHelper with(String authority, String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return new AuthorityBuilder(cupboard().getRegisteredEntities()).forBaseUri(Uri.parse("content://" + authority + path));
    }

    private void addAll(Collection<Class<?>> entities) {
        for (Class<?> clz : entities) {
            add(clz);
        }
    }

    @SuppressLint("DefaultLocale")
    private void add(Class<?> clz) {
        add(clz, clz.getName().toLowerCase());
    }

    private void add(Class<?> clz, String path) {
        int index = mEntities.indexOf(clz);
        if (index > -1) {
            throw new IllegalStateException("Entity "+clz + " already added.");
        }
        mMatcher.addURI(mAuthority, path, mRuleCount++);
        mMatcher.addURI(mAuthority, path + "/#", mRuleCount++);
        mEntities.add(clz);
        mPaths.add(path);
    }

    /**
     * Get the matched entity class
     * @param uri the uri
     * @return the entity class
     * @throws java.lang.IllegalArgumentException if the uri doesn't match any entity class
     */
    public Class<?> getMatchedClass(Uri uri) {
        int index = mMatcher.match(uri);
        if (index > UriMatcher.NO_MATCH) {
            return mEntities.get(index / 2);
        }
        throw new IllegalArgumentException("Uri " + uri.toString()+" does not match any class");
    }

    /**
     * Get the uri for a given entity class
     * @param clz the entity class
     * @return the uri
     * @throws java.lang.IllegalArgumentException if the entity class doest not map to an uri
     */
    public Uri getUri(Class<?> clz) {
        int index = mEntities.indexOf(clz);
        if (index > -1) {
            return Uri.parse("content://" + mAuthority + "/" + mPaths.get(index));
        }
        throw new IllegalArgumentException("Class " + clz.getName()+" cannot be mapped to an uri");
    }

    /**
     * Test if the uri matches any entity classes
     * @param uri the uri to check
     * @return true if the uri matches, false otherwise
     */
    public boolean matches(Uri uri) {
        return mMatcher.match(uri) > UriMatcher.NO_MATCH;
    }

    /**
     * Test if this uri represents a collection of entities
     * @param uri the uri
     * @return true if this uri represents a collection of entities, false otherwise
     */
    public boolean isCollection(Uri uri) {
        int index = mMatcher.match(uri);
        if (index > UriMatcher.NO_MATCH) {
            return (index % 2) == 0;
        }
        return false;
    }

    /**
     * Get the base uri for this matcher
     * @return the base uri
     */
    public Uri getBaseUri() {
        return mBaseUri;
    }

    public static class AuthorityBuilder {
        private List<Class<?>> mEntities;

        private AuthorityBuilder(Collection<Class<?>> entities) {
            mEntities = new ArrayList<>(entities);
        }

        public UriHelper forBaseUri(Uri baseUri) {
            if (!"content://".equals(baseUri.getScheme())) {
                throw new IllegalArgumentException("scheme should be content://");
            }
            return new UriHelper(baseUri, mEntities);
        }

        public UriHelper forAuthority(String authority) {
            return new UriHelper(Uri.parse("content://" +authority), mEntities);
        }
    }

}