package cdf.offer

case class Offer(title: String,
                 url: String,
                 price: BigDecimal,
                 author: String = "",
                 description: String = "") {
  def completeDescription: String = {
    List(title, author, description).mkString("\n")
  }

  override def toString: String = {
    s"Offer($url, $title, $author, $price)"
  }
}
