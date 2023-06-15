/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3.responsive

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.android.launcher3.AbstractDeviceProfileTest
import com.android.launcher3.testing.shared.ResourceUtils
import com.android.launcher3.tests.R
import com.android.launcher3.util.TestResourceHelper
import com.android.launcher3.workspace.CalculatedWorkspaceSpec
import com.android.launcher3.workspace.WorkspaceSpec
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class FolderSpecsTest : AbstractDeviceProfileTest() {
    override val runningContext: Context = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun setup() {
        initializeVarsForPhone(deviceSpecs["tablet"]!!)
    }

    @Test
    fun parseValidFile() {
        val resourceHelper = TestResourceHelper(context!!, R.xml.valid_folders_specs)
        val folderSpecs = FolderSpecs(resourceHelper)

        val sizeSpec16 = SizeSpec(16f.dpToPx())
        val widthSpecsExpected =
            listOf(
                FolderSpec(
                    maxAvailableSize = 800.dpToPx(),
                    specType = FolderSpec.SpecType.WIDTH,
                    startPadding = sizeSpec16,
                    endPadding = sizeSpec16,
                    gutter = sizeSpec16,
                    cellSize = SizeSpec(matchWorkspace = true)
                ),
                FolderSpec(
                    maxAvailableSize = 9999.dpToPx(),
                    specType = FolderSpec.SpecType.WIDTH,
                    startPadding = sizeSpec16,
                    endPadding = sizeSpec16,
                    gutter = sizeSpec16,
                    cellSize = SizeSpec(102f.dpToPx())
                )
            )

        val heightSpecsExpected =
            FolderSpec(
                maxAvailableSize = 9999.dpToPx(),
                specType = FolderSpec.SpecType.HEIGHT,
                startPadding = SizeSpec(24f.dpToPx()),
                endPadding = SizeSpec(64f.dpToPx()),
                gutter = sizeSpec16,
                cellSize = SizeSpec(matchWorkspace = true)
            )

        assertThat(folderSpecs.widthSpecs.size).isEqualTo(widthSpecsExpected.size)
        assertThat(folderSpecs.widthSpecs[0]).isEqualTo(widthSpecsExpected[0])
        assertThat(folderSpecs.widthSpecs[1]).isEqualTo(widthSpecsExpected[1])

        assertThat(folderSpecs.heightSpecs.size).isEqualTo(1)
        assertThat(folderSpecs.heightSpecs[0]).isEqualTo(heightSpecsExpected)
    }

    @Test(expected = IllegalStateException::class)
    fun parseInvalidFile_missingTag_throwsError() {
        val resourceHelper = TestResourceHelper(context!!, R.xml.invalid_folders_specs_1)
        FolderSpecs(resourceHelper)
    }

    @Test(expected = IllegalStateException::class)
    fun parseInvalidFile_moreThanOneValuePerTag_throwsError() {
        val resourceHelper = TestResourceHelper(context!!, R.xml.invalid_folders_specs_2)
        FolderSpecs(resourceHelper)
    }

    @Test(expected = IllegalStateException::class)
    fun parseInvalidFile_valueBiggerThan1_throwsError() {
        val resourceHelper = TestResourceHelper(context!!, R.xml.invalid_folders_specs_3)
        FolderSpecs(resourceHelper)
    }

    @Test(expected = IllegalStateException::class)
    fun parseInvalidFile_missingSpecs_throwsError() {
        val resourceHelper = TestResourceHelper(context!!, R.xml.invalid_folders_specs_4)
        FolderSpecs(resourceHelper)
    }

    @Test(expected = IllegalStateException::class)
    fun parseInvalidFile_missingWidthBreakpoint_throwsError() {
        val availableSpace = 900.dpToPx()
        val cells = 3

        val workspaceSpec =
            WorkspaceSpec(
                maxAvailableSize = availableSpace,
                specType = WorkspaceSpec.SpecType.WIDTH,
                startPadding = SizeSpec(fixedSize = 10f),
                endPadding = SizeSpec(fixedSize = 10f),
                gutter = SizeSpec(fixedSize = 10f),
                cellSize = SizeSpec(fixedSize = 10f)
            )
        val calculatedWorkspaceSpec = CalculatedWorkspaceSpec(availableSpace, cells, workspaceSpec)

        val resourceHelper = TestResourceHelper(context!!, R.xml.invalid_folders_specs_5)
        val folderSpecs = FolderSpecs(resourceHelper)
        folderSpecs.getWidthSpec(cells, availableSpace, calculatedWorkspaceSpec)
    }

    @Test(expected = IllegalStateException::class)
    fun parseInvalidFile_missingHeightBreakpoint_throwsError() {
        val availableSpace = 900.dpToPx()
        val cells = 3

        val workspaceSpec =
            WorkspaceSpec(
                maxAvailableSize = availableSpace,
                specType = WorkspaceSpec.SpecType.HEIGHT,
                startPadding = SizeSpec(fixedSize = 10f),
                endPadding = SizeSpec(fixedSize = 10f),
                gutter = SizeSpec(fixedSize = 10f),
                cellSize = SizeSpec(fixedSize = 10f)
            )
        val calculatedWorkspaceSpec = CalculatedWorkspaceSpec(availableSpace, cells, workspaceSpec)

        val resourceHelper = TestResourceHelper(context!!, R.xml.invalid_folders_specs_5)
        val folderSpecs = FolderSpecs(resourceHelper)
        folderSpecs.getHeightSpec(cells, availableSpace, calculatedWorkspaceSpec)
    }

    @Test
    fun retrievesCalculatedWidthSpec() {
        val availableSpace = 800.dpToPx()
        val cells = 3

        val workspaceSpec =
            WorkspaceSpec(
                maxAvailableSize = availableSpace,
                specType = WorkspaceSpec.SpecType.WIDTH,
                startPadding = SizeSpec(fixedSize = 10f),
                endPadding = SizeSpec(fixedSize = 10f),
                gutter = SizeSpec(fixedSize = 10f),
                cellSize = SizeSpec(fixedSize = 10f)
            )
        val calculatedWorkspaceSpec = CalculatedWorkspaceSpec(availableSpace, cells, workspaceSpec)

        val expectedResult =
            CalculatedFolderSpec(
                startPaddingPx = 16.dpToPx(),
                endPaddingPx = 16.dpToPx(),
                gutterPx = 16.dpToPx(),
                cellSizePx = calculatedWorkspaceSpec.cellSizePx,
                availableSpace = availableSpace,
                cells = cells
            )

        val resourceHelper = TestResourceHelper(context!!, R.xml.valid_folders_specs)
        val folderSpecs = FolderSpecs(resourceHelper)
        val calculatedWidthSpec =
            folderSpecs.getWidthSpec(cells, availableSpace, calculatedWorkspaceSpec)
        assertThat(calculatedWidthSpec).isEqualTo(expectedResult)
    }

    @Test(expected = IllegalStateException::class)
    fun retrievesCalculatedWidthSpec_invalidCalculatedWorkspaceSpecType_throwsError() {
        val availableSpace = 10.dpToPx()
        val cells = 3

        val workspaceSpec =
            WorkspaceSpec(
                maxAvailableSize = availableSpace,
                specType = WorkspaceSpec.SpecType.HEIGHT,
                startPadding = SizeSpec(fixedSize = 10f),
                endPadding = SizeSpec(fixedSize = 10f),
                gutter = SizeSpec(fixedSize = 10f),
                cellSize = SizeSpec(fixedSize = 10f)
            )
        val calculatedWorkspaceSpec = CalculatedWorkspaceSpec(availableSpace, cells, workspaceSpec)

        val resourceHelper = TestResourceHelper(context!!, R.xml.valid_folders_specs)
        val folderSpecs = FolderSpecs(resourceHelper)
        folderSpecs.getWidthSpec(cells, availableSpace, calculatedWorkspaceSpec)
    }

    @Test
    fun retrievesCalculatedHeightSpec() {
        val availableSpace = 700.dpToPx()
        val cells = 3

        val workspaceSpec =
            WorkspaceSpec(
                maxAvailableSize = availableSpace,
                specType = WorkspaceSpec.SpecType.HEIGHT,
                startPadding = SizeSpec(fixedSize = 10f),
                endPadding = SizeSpec(fixedSize = 10f),
                gutter = SizeSpec(fixedSize = 10f),
                cellSize = SizeSpec(fixedSize = 10f)
            )
        val calculatedWorkspaceSpec = CalculatedWorkspaceSpec(availableSpace, cells, workspaceSpec)

        val expectedResult =
            CalculatedFolderSpec(
                startPaddingPx = 24.dpToPx(),
                endPaddingPx = 64.dpToPx(),
                gutterPx = 16.dpToPx(),
                cellSizePx = calculatedWorkspaceSpec.cellSizePx,
                availableSpace = availableSpace,
                cells = cells
            )

        val resourceHelper = TestResourceHelper(context!!, R.xml.valid_folders_specs)
        val folderSpecs = FolderSpecs(resourceHelper)
        val calculatedHeightSpec =
            folderSpecs.getHeightSpec(cells, availableSpace, calculatedWorkspaceSpec)
        assertThat(calculatedHeightSpec).isEqualTo(expectedResult)
    }

    @Test(expected = IllegalStateException::class)
    fun retrievesCalculatedHeightSpec_invalidCalculatedWorkspaceSpecType_throwsError() {
        val availableSpace = 10.dpToPx()
        val cells = 3

        val workspaceSpec =
            WorkspaceSpec(
                maxAvailableSize = availableSpace,
                specType = WorkspaceSpec.SpecType.WIDTH,
                startPadding = SizeSpec(fixedSize = 10f),
                endPadding = SizeSpec(fixedSize = 10f),
                gutter = SizeSpec(fixedSize = 10f),
                cellSize = SizeSpec(fixedSize = 10f)
            )
        val calculatedWorkspaceSpec = CalculatedWorkspaceSpec(availableSpace, cells, workspaceSpec)

        val resourceHelper = TestResourceHelper(context!!, R.xml.valid_folders_specs)
        val folderSpecs = FolderSpecs(resourceHelper)
        folderSpecs.getHeightSpec(cells, availableSpace, calculatedWorkspaceSpec)
    }

    private fun Float.dpToPx(): Float {
        return ResourceUtils.pxFromDp(this, context!!.resources.displayMetrics).toFloat()
    }

    private fun Int.dpToPx(): Int {
        return ResourceUtils.pxFromDp(this.toFloat(), context!!.resources.displayMetrics)
    }
}
