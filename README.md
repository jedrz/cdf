# cdf

*cdf* searches for book title query, groups the same book offers and
sorts by price.

Written in scala with akka.

## Clustering

Book clustering is done with k-medoids algorithm. To compare book offers n-gram
based and tf/idf distance measures can be used. Since k-medoids needs
predefined number of clusters modified silhouette validation method is used to
evaluate the quality of clustering and find the best number of groups.

## Usage

    sbt "run <query-1> ... <query-n>"

Example:

    sbt "run ubik \"pustynna włócznia\" \"nocny patrol\""

## Authors

- [Łukasz Jędrzejewski](https://github.com/jedrz)
- [Igor Rodzik](https://github.com/irodzik)
- [Artur Sawicki](https://github.com/tempaowca)
