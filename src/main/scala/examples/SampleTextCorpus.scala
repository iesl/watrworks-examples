package examples

import edu.umass.cs.iesl.watr._
import corpora._
// import TypeTags._
import textboxing.{TextBoxing => TB}, TB._

object TextPageSamples {
  val samplePages = List(
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


class SampleTextCorpus extends PlainTextCorpus {
  override val docStore: DocumentCorpus = new MemDocstore

  def loadSampleDoc(stableId: String@@DocumentID, pageCount: Int): Unit = {
    val pages = TextPageSamples.samplePages
      .take(pageCount)

    addDocument(stableId, pages)
  }

  def reportDocument(stableId: String@@DocumentID): TB.Box = {
    val docBoxes = for {
      docId <- docStore.getDocument(stableId).toSeq
    } yield {
      val pagesBox = for {
        pageId <- docStore.getPages(docId)
      } yield {
        val pageGeometry = docStore.getPageGeometry(pageId)

        val allTargetRegions = docStore.getTargetRegions(pageId)

        val regionCount = s"TargetRegions for page ${pageId}: ${allTargetRegions.length} ".box


        (indent(2)("PageGeometry")
          % indent(4)(pageGeometry.toString.box)
          % indent(2)(regionCount)
          % indent(2)("Page Zones")
        )
      }

      val zoneBoxes = for {
        zoneId <- docStore.getZonesForDocument(docId)
        textReflow <- docStore.getTextReflowForZone(zoneId)
      } yield {
        (textReflow.toText.box
          % docStore.getZone(zoneId).toString().box)
      }


      (s"Document ${docId} (${stableId})"
        % indent(4)(vcat(pagesBox))
        % indent(2)("Zones")
        % indent(4)(vcat(zoneBoxes))
      )
    }
    vcat(docBoxes)
  }


}
