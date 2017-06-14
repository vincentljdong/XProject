package com.jaydon.xproject.fragment;

import com.jaydon.xproject.base.BaseFragment;
import com.jaydon.xproject.delegate.OneFragmentDelegate;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/13 11:44
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class OneFragment extends BaseFragment<OneFragmentDelegate> {
    @Override
    protected Class<OneFragmentDelegate> getDelegateClass() {
        return OneFragmentDelegate.class;
    }
}
