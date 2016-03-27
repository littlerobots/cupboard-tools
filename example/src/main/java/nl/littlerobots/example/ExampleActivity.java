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

package nl.littlerobots.example;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import nl.littlerobots.cupboard.tools.widget.CupboardCursorAdapter;
import nl.littlerobots.example.model.Cheese;
import nl.littlerobots.example.provider.MyProvider;

public class ExampleActivity extends ListActivity implements LoaderCallbacks<Cursor> {

    private CheeseAdapter mAdapter;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(this);
        loader.setUri(UriHelper.with(MyProvider.AUTHORITY).getUri(Cheese.class));
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new CheeseAdapter(this);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    private static class CheeseAdapter extends CupboardCursorAdapter<Cheese> {

        public CheeseAdapter(Context context) {
            super(context, Cheese.class);
        }

        @Override
        public View newView(Context context, Cheese model, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cheese cheese) {
            ((TextView) view).setText(cheese.name);
        }
    }
}
