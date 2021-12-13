package com.example.sportsbet.interfaces

import com.example.sportsbet.models.MatchDetailsModel

interface Communication {

    fun addToList(match: MatchDetailsModel, check: Boolean)

    fun changeItems(match: MatchDetailsModel)
}