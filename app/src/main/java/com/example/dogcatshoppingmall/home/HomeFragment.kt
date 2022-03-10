package com.example.dogcatshoppingmall.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dogcatshoppingmall.R
import com.example.dogcatshoppingmall.databinding.FragmentHomeBinding
import com.example.dogcatshoppingmall.home.mainPatType.MainCatTypeFragment
import com.example.dogcatshoppingmall.home.mainPatType.MainDogTypeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }

    //전역 변수로 바인딩 객체 선언
    private var mBinding: FragmentHomeBinding? = null
    //편의성을 위한 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout {
        mBinding = FragmentHomeBinding.inflate(inflater,container, false)

        val dogFragment = MainDogTypeFragment()
        val catFragment = MainCatTypeFragment()

        val fragments = arrayListOf<Fragment>(dogFragment, catFragment)

        val tabAdapter = object : FragmentStateAdapter(this) {

            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }
        binding.viewPager2.adapter = tabAdapter

        TabLayoutMediator(binding.mainTypeTabLayout, binding.viewPager2) {tab,position ->
            when (position) {
                0 -> tab.setText(R.string.tabDogText)
                else -> tab.setText(R.string.tabCatText)
            }
        }.attach()

        return binding.root
    }


    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}