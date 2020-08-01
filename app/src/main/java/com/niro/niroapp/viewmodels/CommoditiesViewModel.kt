package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.niro.niroapp.R
import com.niro.niroapp.adapters.CommoditiesListAdapter
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.postdatamodels.UpdateCommoditiesPostData
import com.niro.niroapp.models.responsemodels.Category
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.models.responsemodels.MandiLocation
import com.niro.niroapp.network.ApiClient
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.utils.ItemClickListener
import com.niro.niroapp.viewmodels.repositories.CategoryListRepository
import com.niro.niroapp.viewmodels.repositories.UpdateCommoditiesRepository

class CommoditiesViewModel : ListViewModel(), FilterResultsListener<CommodityItem> {

    private val categories = MutableLiveData<MutableList<Category>>()

    private val categoriesNameMap = MutableLiveData<HashMap<String, Category>>(HashMap())

    private val commoditiesList = MutableLiveData<MutableList<CommodityItem>>(ArrayList<CommodityItem>().toMutableList())

    private val selectedCommodities = MutableLiveData<ArrayList<CommodityItem>>(ArrayList())

    private var commoditiesAdapter: CommoditiesListAdapter? = null

    private lateinit var clickItemListener: ItemClickListener

    init {
        commoditiesList.value?.clear()
        commoditiesAdapter = CommoditiesListAdapter(getViewType(), commoditiesList.value, this)
    }

    fun getAdapter() : CommoditiesListAdapter? = commoditiesAdapter


    fun getSelectedCommoditiesList() = selectedCommodities

    fun setCategories(categoriesList: MutableList<Category>) {
        this.categories.value = categoriesList
        updateCommoditiesList()
        updateCategoriesNameMap()
    }

    private fun updateCategoriesNameMap(): MutableLiveData<HashMap<String, Category>> {
        val categoriesMap = HashMap<String, Category>()


        categories.value?.forEach { category ->
            categoriesMap[category.name] = category
        }


        categoriesNameMap.value = categoriesMap

        return categoriesNameMap
    }


    private fun updateCommoditiesList(): MutableLiveData<MutableList<CommodityItem>> {

        val commodityItemList = ArrayList<CommodityItem>()

        categories.value?.forEach { category ->

            val itemList = category.commodities.map { commodity ->
                CommodityItem(
                    id = commodity.id, image = commodity.image,
                    name = commodity.name, categoryName = category.name
                )
            }

            commodityItemList.addAll(itemList.toMutableList())
        }

        commoditiesList.value = commodityItemList
        return commoditiesList
    }


    fun getAllCommodities(context : Context?): MutableLiveData<APIResponse> {
        val categoryRepository = CategoryListRepository<List<Category>>(context)
        return categoryRepository.getResponse()
    }


    override fun getViewType(): Int {
        return R.layout.card_commodity_item

    }


    override fun updateList() {
        commoditiesAdapter?.updateList(commoditiesList.value)
    }

    fun setSelectedCommodities(selectedCommodities: MutableList<CommodityItem>?) {

        if(selectedCommodities == null || selectedCommodities.isEmpty()) return

        selectedCommodities.forEach { selectedCommodity ->
            commoditiesList.value?.find { commodityItem -> commodityItem.name.equals(selectedCommodity.name) }?.isSelected = true
        }
    }


    override fun onResultsFiltered(filteredList: List<CommodityItem>) {
        commoditiesList.value?.clear()
        commoditiesList.value = filteredList.toMutableList()
       commoditiesAdapter?.updateFilterList(filteredList.toMutableList())
    }

    fun updateCommodities(currentUserId: String?, context: Context?) : MutableLiveData<APIResponse>{

        val updateCommoditiesPostData = UpdateCommoditiesPostData(currentUserId,selectedCommodities.value)

        return UpdateCommoditiesRepository(updateCommoditiesPostData,context).getResponse()
    }


}