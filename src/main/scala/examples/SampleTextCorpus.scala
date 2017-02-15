package examples

import edu.umass.cs.iesl.watr._
import corpora._
// import TypeTags._
import textboxing.{TextBoxing => TB}, TB._

class SampleTextCorpus extends PlainTextCorpus {
  override val docStore: DocumentCorpus = new MemDocstore


  def loadSampleDoc(stableId: String@@DocumentID, pageCount: Int): Unit = {
    val pages = TextPageSamples.samples
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
