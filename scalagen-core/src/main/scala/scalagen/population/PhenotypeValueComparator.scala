package scalagen.population

trait PhenotypeValueComparator {
  
  /**
   * An function to determine if received value is better than current.
   * Example:
   * override def isBetterValue(currentValue: Double, newValue: Double) = currentValue < newValue
   */
  def isBetterValue(currentValue: Double, newValue: Double): Boolean
}
