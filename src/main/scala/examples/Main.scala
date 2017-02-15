package examples

import edu.umass.cs.iesl.watr._
import corpora._
// import textboxing.{TextBoxing => TB}, TB._
import textreflow.data._
import TypeTags._

object TextPageSamples {
  val samples = List(
    """|            The Title of the Paper
       |^{a}Faculty of Engineering, Yamagata University, Yonezawa 992-8510, Japan
       |""".stripMargin,

    """|   EXPERIMENTAL
       |1. Sample Preparation and Characterization
       |
       |   The starting material of NaBiO_{3} ? nH2O (Nacalai Tesque
       |Inc.) was placed in a Teflon lined autoclave (70 ml) with
       |LiOH and H2O (30 ml) and was heated at 120–2008C
       |for 4 days.
       |
       |""".stripMargin
  )

}

object Examples extends App {
  override def main(args: Array[String]) = {

    val corpus = new SampleTextCorpus()

    //
    val stableId = DocumentID("some-id#23")

    corpus.loadSampleDoc(stableId, 2)

    reportDocuments(corpus.docStore)
  }

  def reportDocuments(docStore: DocumentCorpus): Unit = {
    for {
      stableId <- docStore.getDocuments()
      docId <- docStore.getDocument(stableId)
    } {

      val pagesBox = for {
        pageId <- docStore.getPages(docId)
      } {
        val pageGeometry = docStore.getPageGeometry(pageId)
        println(s"Page ${pageId}")

        println("PageGeometry")
        println(pageGeometry)

        val allTargetRegions = docStore.getTargetRegions(pageId)

        println(s"TargetRegion count: ${allTargetRegions.length} ")

      }

      println("All Document Zones")

      for {
        zoneId <- docStore.getZonesForDocument(docId)
        textReflow <- docStore.getTextReflowForZone(zoneId)
      } {
        println("Zone:")
        println(docStore.getZone(zoneId))
        println("Text:")
        println(textReflow.toText)
      }


      println(s"Document ${docId} (${stableId})")

    }
  }



}
