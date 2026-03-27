package com.joao.warehouse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.joao.warehouse.data.model.MovementType
import com.joao.warehouse.ui.theme.StockIn
import com.joao.warehouse.ui.theme.StockOut
import com.joao.warehouse.ui.viewmodel.StockMovementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockMovementScreen(
    viewModel: StockMovementViewModel,
    preSelectedProductId: Long?,
    onNavigateBack: () -> Unit
) {
    val allProducts by viewModel.allProducts.collectAsState()

    var selectedProductId by remember { mutableLongStateOf(preSelectedProductId ?: -1L) }
    var movementType by remember { mutableStateOf(MovementType.IN) }
    var quantity by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var productDropdownExpanded by remember { mutableStateOf(false) }

    var quantityError by remember { mutableStateOf(false) }
    var productError by remember { mutableStateOf(false) }

    var selectedProductName by remember { mutableStateOf("") }

    LaunchedEffect(preSelectedProductId, allProducts) {
        if (preSelectedProductId != null) {
            val product = viewModel.getProductById(preSelectedProductId)
            product?.let {
                selectedProductName = it.name
                selectedProductId = it.id
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movimentar Stock") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Tipo de Movimento",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = movementType == MovementType.IN,
                    onClick = { movementType = MovementType.IN },
                    label = { Text("Entrada") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StockIn.copy(alpha = 0.2f),
                        selectedLabelColor = StockIn
                    ),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = movementType == MovementType.OUT,
                    onClick = { movementType = MovementType.OUT },
                    label = { Text("Saida") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StockOut.copy(alpha = 0.2f),
                        selectedLabelColor = StockOut
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            ExposedDropdownMenuBox(
                expanded = productDropdownExpanded,
                onExpandedChange = { productDropdownExpanded = !productDropdownExpanded }
            ) {
                val displayName = if (selectedProductId != -1L) {
                    allProducts.find { it.id == selectedProductId }?.name ?: selectedProductName
                } else {
                    ""
                }
                OutlinedTextField(
                    value = displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Produto *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDropdownExpanded) },
                    isError = productError,
                    supportingText = if (productError) {
                        { Text("Selecione um produto") }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = productDropdownExpanded,
                    onDismissRequest = { productDropdownExpanded = false }
                ) {
                    if (allProducts.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Nenhum produto disponivel") },
                            onClick = { productDropdownExpanded = false },
                            enabled = false
                        )
                    } else {
                        allProducts.forEach { product ->
                            DropdownMenuItem(
                                text = {
                                    Text("${product.name} (Qtd: ${product.quantity})")
                                },
                                onClick = {
                                    selectedProductId = product.id
                                    selectedProductName = product.name
                                    productError = false
                                    productDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = {
                    quantity = it.filter { c -> c.isDigit() }
                    quantityError = false
                },
                label = { Text("Quantidade *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = quantityError,
                supportingText = if (quantityError) {
                    { Text("Insira uma quantidade valida") }
                } else null,
                singleLine = true
            )

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Motivo / Observacoes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    productError = selectedProductId == -1L
                    val qty = quantity.toIntOrNull()
                    quantityError = qty == null || qty <= 0

                    if (!productError && !quantityError) {
                        viewModel.registerMovement(
                            productId = selectedProductId,
                            type = movementType,
                            quantity = qty!!,
                            reason = reason.trim()
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (movementType == MovementType.IN) StockIn else StockOut
                )
            ) {
                Text(
                    if (movementType == MovementType.IN) "Registar Entrada" else "Registar Saida"
                )
            }
        }
    }
}
