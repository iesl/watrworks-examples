package examples

import edu.umass.cs.iesl.watr._
import corpora._
// import textboxing.{TextBoxing => TB}, TB._
import textreflow.data._
import TypeTags._
import geometry._
import GeometryImplicits._
import PageComponentImplicits._
import utils.{CompassDirection => Compass}

object Examples extends App {
  override def main(args: Array[String]) = {

    val corpus = new SampleTextCorpus()

    // Stable IDs are arbitrary strings, but must be unique within a given corpus,
    //   often just a filename
    val stableId = DocumentID("some-id#23")

    // Load a 2-page document sample w/give id
    corpus.loadSampleDoc(stableId, 2)

    reportDocuments(stableId, corpus.docStore)
  }

  def reportDocuments(stableId: String@@DocumentID, docStore: DocumentCorpus): Unit = {
    for {
      docId <- docStore.getDocument(stableId)
    } {
      println(s"Document ${docId} (${stableId})")

      for {
        pageId <- docStore.getPages(docId)
      } {
        println(s"Page ${pageId}")

        // Every page has a geometry bounding box
        val pageGeometry = docStore.getPageGeometry(pageId)
        println("PageGeometry")
        println(pageGeometry)

        // A TargetRegion is a bounding box for a specific document page:
        val allTargetRegions = docStore.getTargetRegions(pageId)

        println(s"TargetRegion count: ${allTargetRegions.length} ")
        val regionId = docStore.getTargetRegions(pageId).head
        val oneTargetRegion = docStore.getTargetRegion(regionId)

        println(s"First TargetRegion: ${oneTargetRegion}")

      }

      println()
      println("All Document Zones")

      // A Zone is an ordered list of TargetRegions, with one or more labels
      // It may also have text associated with it, in the form of a TextReflow.
      //   (more on TextReflows later)
      //
      // Documents are initially segmented into zones labeled VisualLine,
      //   corresponding to the lines on a page
      for {
        zoneId <- docStore.getZonesForDocument(docId)
        textReflow <- docStore.getTextReflowForZone(zoneId)
      } {
        val zone = docStore.getZone(zoneId)
        val ls = zone.labels
        val zoneTargetRegions = zone.regions
        println("  " + zone)
        println("  " + textReflow.toText)

        // TextReflow is important: here's what it can do:
        // Act much like a normal string:
        val len = textReflow.length
        val text = textReflow.toText()

        // Compute the bounding box within which the text appears
        textReflow.targetRegion()
        // Compute all char-level bounding boxes for the text
        textReflow.targetRegions()

        // Examine the individual chars w/bounding boxes
        textReflow.charAtoms().take(3).foreach { charAtom =>
          println(s"${charAtom.char}: ${charAtom.targetRegion}")
        }

        // textReflow.slice(begin: Int, end: Int)
        // Sliding trigram example:
        for {
          i <- 0 until textReflow.length
          slice <- textReflow.slice(i, i+3)
        }  {
          println(
            s"""Tri: ${slice.toText()} ${slice.targetRegion().bbox}"""
          )
        }

      }

      val allTextReflows = for {
        zoneId <- docStore.getZonesForDocument(docId)
        textReflow <- docStore.getTextReflowForZone(zoneId)
      } yield textReflow

      //
      val reflow1 = allTextReflows.head
      val reflow2 = allTextReflows.drop(1).head

      // Here are some useful operations on TargetRegions:
      // Union: (the regions must be on the same page for union to work, otherwise it's a runtime error)
      val reflow12Region = reflow1.targetRegion.union(reflow2.targetRegion)
      // Intersects (Boolean)
      reflow1.targetRegion.intersects(reflow2.targetRegion) == false
      reflow12Region.intersects(reflow1.targetRegion()) == true

      // And here are operations on Bounding boxes of type LTBounds (left, top, width, height)

      // Bounds of two TextReflows:
      val reflow1Bounds:LTBounds = reflow1.targetRegion.bbox
      val reflow2Bounds:LTBounds = reflow2.targetRegion.bbox

      val area: Double = reflow1Bounds.area
      // Find top/bottom/left/etc 
      val top: Double = reflow1Bounds.top // or .left  .right  .bottom

      // Move it..
      reflow1Bounds.translate(x=3.0, y=3.4)

      // Find the center point
      val p0: Point = reflow1Bounds.toCenterPoint
      // p0.x;  p0.y


      // Find the corner points
      val point1 = reflow1Bounds.toPoint(Compass.NE) // or .NW, .SE, .SW
      val point2 = reflow2Bounds.toPoint(Compass.NE) // or .NW, .SE, .SW

      // find distances
      val p12Dist: Double = point1.dist(point2)


    }
  }



}
