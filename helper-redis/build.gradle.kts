plugins{
    id("pineta-conventions")
}

dependencies{
    compileOnlyApi(libs.lettuce.lib)
    compileOnlyApi(libs.apache.commons.pool3.lib)
    compileOnlyApi(libs.alibaba.fastjson2.lib)
}