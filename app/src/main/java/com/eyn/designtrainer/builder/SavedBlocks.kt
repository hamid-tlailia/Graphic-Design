package com.eyn.designtrainer.builder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eyn.designtrainer.data.AppData
import com.eyn.designtrainer.data.AppViewModel
import com.eyn.designtrainer.data.Block
import com.eyn.designtrainer.data.BlockKind
import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.data.SavedBlock
import com.eyn.designtrainer.data.newId
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type

/** A category's display name: a block kind's label, or the name the user typed. */
@Composable
fun categoryLabel(category: String): String =
    BlockKind.entries.firstOrNull { it.name == category }?.label()?.t() ?: category

/** Saves [b] (and its drawing, for a free block) as a reusable block. */
fun savedFrom(b: Block, data: AppData, name: String, category: String): SavedBlock {
    val drawing = data.designs.firstOrNull { it.id == b.designId }?.let { it.copy(id = newId(), kind = BLOCK_DESIGN) }
    return SavedBlock(name = name, category = category, block = b.copy(id = newId(), designId = drawing?.id ?: ""), design = drawing)
}

/** Saves a whole canvas design as a free block. */
fun savedFromDesign(d: Design, name: String, category: String): SavedBlock {
    val drawing = d.copy(id = newId(), kind = BLOCK_DESIGN)
    val block = Block(kind = BlockKind.CUSTOM, variant = CV.FREE, padding = 0f, radius = 0f, designId = drawing.id)
    return SavedBlock(name = name, category = category, block = block, design = drawing)
}

/** A fresh block for a page, with its own copy of any drawing so edits stay separate. */
fun SavedBlock.instantiate(vm: AppViewModel): Block {
    val drawing = design?.copy(id = newId())
    if (drawing != null) vm.saveDesign(drawing)
    return block.copy(id = newId(), designId = drawing?.id ?: block.designId)
}

/** Data with the saved blocks' drawings added, so their previews can render. */
fun AppData.withSavedDrawings(): AppData = copy(designs = designs + savedBlocks.mapNotNull { it.design })

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SaveBlockDialog(defaultName: String, defaultCategory: String, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    val c = Eyn.c
    val data = LocalData.current
    var name by remember { mutableStateOf(defaultName) }
    var category by remember { mutableStateOf(defaultCategory) }
    var custom by remember { mutableStateOf("") }
    val kindNames = BlockKind.entries.map { it.name }.toSet()
    val ownCategories = data.savedBlocks.map { it.category }.filter { it !in kindNames }.distinct()
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.surface,
        title = { Text(tr("حفظ القطعة لاستعمالها لاحقاً", "Save block for later"), style = Type.h3, color = c.text) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    name, { name = it.take(40) }, singleLine = true, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(),
                    label = { Text(tr("اسم القطعة", "Block name")) },
                )
                Spacer(Modifier.height(12.dp))
                Text(tr("احفظها في قسم", "Save it under"), style = Type.label, color = c.muted)
                Spacer(Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (BlockKind.entries.map { it.name } + ownCategories).forEach { cat ->
                        OptionChip(categoryLabel(cat), category == cat && custom.isBlank()) { category = cat; custom = "" }
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    custom, { custom = it.take(24) }, singleLine = true, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(),
                    label = { Text(tr("أو قسم جديد باسمك", "Or a new section")) },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name.ifBlank { defaultName }, custom.trim().ifBlank { category }) }) { Text(tr("حفظ", "Save")) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(tr("إلغاء", "Cancel")) } },
    )
}
