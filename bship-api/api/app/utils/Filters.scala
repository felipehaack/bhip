package utils

import javax.inject.Inject
import play.filters.cors.CORSFilter
import play.api.http.DefaultHttpFilters

class Filters @Inject()(corsFilter: CORSFilter) extends DefaultHttpFilters(corsFilter)