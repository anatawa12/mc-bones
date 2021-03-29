val size = args[0].toInt()

val variables = listOf("x", "y", "z", "w").take(size)

fun commaed(sep: String = ", ", transform: (String)->String): String {
    return variables.joinToString(sep, transform = transform)
}

val type = "Vec${size}f"

println("package com.anatawa12.mbBones.math;")
println("")
println("import org.jetbrains.annotations.NotNull;")
println("")
println("/*")
println(" * geneated by vecgen.kts")
println(" */")
println("")
println("/**")
println(" * simple vector of $size floats.")
when (size) {
    2 -> {
        println(" * for UV, x: u and y: v")
    }
    3 -> {
        println(" * for rotation, x:pitch y:heading z: bank")
        println(" * Rotate heading first, then pitch and bank.")
    }
    4 -> {
        println(" * as a quotanion, w + ix + jy + kz.")
    }
}
println(" */")
println("public class $type {")
for (variable in variables) {
    println("    public final float $variable;")
}
println("")
println("    public static $type ORIGIN = new $type(${commaed { "0" } });")
println("")
println("    public $type(${commaed { "float $it" } }) {")
for (variable in variables) {
    println("        this.$variable = $variable;")
}
println("    }")
println("")
println("    public @NotNull $type sub(@NotNull $type pos) {")
println("        return sub(${commaed { "pos.$it" } });")
println("    }")
println("")
println("    private @NotNull $type sub(${commaed { "float $it" } }) {")
println("        return new $type(${commaed { "this.$it - $it" } });")
println("    }")
println("")
println("    public @NotNull $type add(@NotNull $type pos) {")
println("        return add(${commaed { "pos.$it" } });")
println("    }")
println("")
println("    public @NotNull $type add(${commaed { "float $it" } }) {")
println("        return new $type(${commaed { "this.$it + $it" } });")
println("    }")
println("")
println("    public @NotNull $type times(float a) {")
println("        return new $type(${commaed { "this.$it * a" } });")
println("    }")
println("")
println("    public @NotNull $type div(float a) {")
println("        return new $type(${commaed { "this.$it / a" } });")
println("    }")
println("")
println("    public @NotNull float norm() {")
println("        return (float)Math.sqrt(${commaed(" + ") { "$it * $it" } });")
println("    }")
println("")
println("    public @NotNull $type normalized() {")
println("        return div(norm());")
println("    }")
println("")
println("    @Override")
println("    public boolean equals(Object other) {")
println("        if (other == null) return false;")
println("        if (other == this) return true;")
println("        if (other.getClass() != $type.class) return false;")
println("        $type that = ($type) other;")
for (variable in variables) {
    println("        if (this.$variable != that.$variable) return false;")
}
println("        return true;")
println("    }")
println("")
println("    @Override")
println("    public int hashCode() {")
println("        int hash = 0;")
for (variable in variables) {
    println("        hash = hash * 31 + Float.hashCode(this.$variable);")
}
println("        return hash;")
println("    }")
println("")
println("    @Override")
println("    public String toString() {")
println("        return \"$type(${commaed { "\" + $it + \"" }})\";")
println("    }")
println("}")
println("")

