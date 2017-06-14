package com.jaydon.xproject.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kymjs.themvp.presenter.FragmentPresenter;
import com.kymjs.themvp.view.IDelegate;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/13 11:37
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public abstract class BaseFragment<T extends IDelegate> extends FragmentPresenter<T> {

}
