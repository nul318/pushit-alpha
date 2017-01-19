package pushit.pushit;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

/**
 * Created by Marcin on 2015-04-14.
 */
public class BSCardActivity extends FragmentActivity {

    BSAdapter mAdapter;
    ViewPager mPager;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        bundle = getIntent().getBundleExtra("data");

        mAdapter = new BSAdapter(getSupportFragmentManager(), bundle);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
    }
}
