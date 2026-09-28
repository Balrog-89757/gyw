package com.baidu.utils;

import java.util.ArrayList;
import java.util.List;

public class SysMenu {
    private String name;
    private String title;
    private String iconClass;
    private boolean child;
    private String href;
    private List<SysMenu> childMenuList;

    public SysMenu() {
        this.childMenuList = new ArrayList<>();
    }

    public SysMenu(String name, String title, String iconClass, boolean child, String href) {
        this.name = name;
        this.title = title;
        this.iconClass = iconClass;
        this.child = child;
        this.href = href;
        this.childMenuList = new ArrayList<>();
    }

    // -------- getter / setter --------
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIconClass() { return iconClass; }
    public void setIconClass(String iconClass) { this.iconClass = iconClass; }

    public boolean isChild() { return child; }
    public void setChild(boolean child) { this.child = child; }

    public String getHref() { return href; }
    public void setHref(String href) { this.href = href; }

    public List<SysMenu> getChildMenuList() { return childMenuList; }
    public void setChildMenuList(List<SysMenu> childMenuList) { this.childMenuList = childMenuList; }
}