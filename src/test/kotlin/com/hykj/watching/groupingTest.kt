package com.hykj.watching

import kotlin.test.Test




class GroupingTest{
    @Test
    fun testGrouping(){
        val positions = listOf(
            Position(3, "b"),
            Position(2, "a"),
            Position(1, "b"),
        )
//        val groups = getOrderedGroup(positions)
        //[(b, [Position(order=1, group=b), Position(order=3, group=b)]), (a, [Position(order=2, group=a)])]
    }
}