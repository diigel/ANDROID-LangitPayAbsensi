package com.absensi.langitpay.absent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.onBack
import kotlinx.android.synthetic.main.activity_confirmation_absen.*

class ConfirmationAbsenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation_absen)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
           onBack()
        }

        vp.offscreenPageLimit = 2
        ViewPagerAdapter(supportFragmentManager).also {viewPagerAdapter ->
            viewPagerAdapter.addFragment(AbsentOfficeFragment(),"Absen Kantor")
            viewPagerAdapter.addFragment(AbsentOutsideTheOfficeFragment(),"Absen Luar Kantor")
            vp.adapter = viewPagerAdapter
        }
        tabs.setupWithViewPager(vp)
    }
}

class ViewPagerAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList = ArrayList<Fragment>()
    private val fragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentTitleList[position]

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }
}