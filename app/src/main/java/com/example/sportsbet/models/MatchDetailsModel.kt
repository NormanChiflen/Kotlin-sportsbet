package com.example.sportsbet.models

data class MatchDetailsModel(val id: String, val home_team: String, val away_team: String, val points: String, val first_price: String, val second_price: String,
                             var checkedUnder: Boolean = false,var checkedOver: Boolean = false)
