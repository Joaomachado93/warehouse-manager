package com.joao.warehouse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.joao.warehouse.data.model.Product
import com.joao.warehouse.ui.viewmodel.CategoryViewModel
import com.joao.warehouse.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productViewModel: ProductViewModel,
    categoryViewModel: CategoryViewModel,
    productId: Long?,
    scannedBarcode: String?,
    onNavigateBack: () -> Unit,
    onNavigateToScanner: () -> Unit
) {
    val categories by categoryViewModel.categories.collectAsState()
    val isEditing = productId != null

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var minStockLevel by remember { mutableStateOf("0") }
    var selectedCategoryId by remember { mutableLongStateOf(-1L) }
    var location by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var existingProduct by remember { mutableStateOf<Product?>(null) }

    var nameError by remember { mutableStateOf(false) }
    var skuError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }

    var createdAtTimestamp by remember { mutableLongStateOf(0L) }
    var updatedAtTimestamp by remember { mutableLongStateOf(0L) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "PT")) }

    // Handle scanned barcode from navigation result
    LaunchedEffect(scannedBarcode) {
        if (!scannedBarcode.isNullOrEmpty()) {
            barcode = scannedBarcode
        }
    }

    LaunchedEffect(productId) {
        if (productId != null) {
            val product = productViewModel.getProductById(productId)
            product?.let {
                existingProduct = it
                name = it.name
                description = it.description
                sku = it.sku
                quantity = it.quantity.toString()
                minStockLevel = it.minStockLevel.toString()
                selectedCategoryId = it.categoryId ?: -1L
                location = it.location
                barcode = it.barcode
                createdAtTimestamp = it.createdAt
                updatedAtTimestamp = it.updatedAt
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditing) "Editar Produto" else "Novo Produto")
                },
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Nome *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("Nome e obrigatorio") }
                } else null,
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descricao") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            OutlinedTextField(
                value = sku,
                onValueChange = {
                    sku = it
                    skuError = false
                },
                label = { Text("SKU *") },
                modifier = Modifier.fillMaxWidth(),
                isError = skuError,
                supportingText = if (skuError) {
                    { Text("SKU e obrigatorio") }
                } else null,
                singleLine = true
            )

            // Barcode field with camera button
            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                label = { Text("Codigo de Barras") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onNavigateToScanner) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Digitalizar Codigo de Barras",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        quantity = it.filter { c -> c.isDigit() }
                        quantityError = false
                    },
                    label = { Text("Quantidade") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = quantityError,
                    supportingText = if (quantityError) {
                        { Text("A quantidade deve ser maior ou igual a 0") }
                    } else null
                )

                OutlinedTextField(
                    value = minStockLevel,
                    onValueChange = { minStockLevel = it.filter { c -> c.isDigit() } },
                    label = { Text("Stock Minimo") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
            ) {
                val selectedCategory = categories.find { it.id == selectedCategoryId }
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Sem categoria",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sem categoria") },
                        onClick = {
                            selectedCategoryId = -1L
                            categoryDropdownExpanded = false
                        }
                    )
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategoryId = category.id
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Localizacao (Prateleira/Fila)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Date fields (read-only, only shown when editing)
            if (isEditing && createdAtTimestamp > 0L) {
                OutlinedTextField(
                    value = dateFormat.format(Date(createdAtTimestamp)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data de Criacao") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    enabled = false
                )
            }

            if (isEditing && updatedAtTimestamp > 0L) {
                OutlinedTextField(
                    value = dateFormat.format(Date(updatedAtTimestamp)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ultima Atualizacao") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    enabled = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    nameError = name.isBlank()
                    skuError = sku.isBlank()
                    val parsedQuantity = quantity.toIntOrNull()
                    quantityError = parsedQuantity == null || parsedQuantity < 0

                    if (!nameError && !skuError && !quantityError) {
                        val now = System.currentTimeMillis()
                        val product = Product(
                            id = existingProduct?.id ?: 0,
                            name = name.trim(),
                            description = description.trim(),
                            sku = sku.trim(),
                            quantity = parsedQuantity ?: 0,
                            minStockLevel = minStockLevel.toIntOrNull() ?: 0,
                            categoryId = if (selectedCategoryId == -1L) null else selectedCategoryId,
                            location = location.trim(),
                            barcode = barcode.trim(),
                            imageUri = existingProduct?.imageUri,
                            createdAt = existingProduct?.createdAt ?: now,
                            updatedAt = now
                        )

                        if (isEditing) {
                            productViewModel.updateProduct(product)
                        } else {
                            productViewModel.addProduct(product)
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar("Produto guardado com sucesso")
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Guardar Alteracoes" else "Adicionar Produto")
            }
        }
    }
}
