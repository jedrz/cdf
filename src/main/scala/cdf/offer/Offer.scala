package cdf.offer

case class Offer(title: String,
                 url: String,
                 price: BigDecimal,
                 author: String = "",
                 description: String = "")
