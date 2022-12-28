import { Ref, ref, computed } from "vue";
import { Filter, FilterValue } from "@/data/filter";
import { getAllCategories } from "@/store/events";

//
export const setFilters = (filters: Filter[]) => {
  localStorage.setItem('filters', JSON.stringify(filters));
}

export const getFilters = () => {
  let filters = localStorage.getItem("filters");
  if (filters != null) {
    return JSON.parse(filters);
  }
  return [];
}

export const refreshFilters = async () => {
  let selectedCategories = getFilteredCategories();
  //
  let listCat: FilterValue[] = [];
  const res = await getAllCategories();
  for (const categoryStr of res) {
    if (selectedCategories.includes(categoryStr))
    {
      listCat.push({name: categoryStr, checked: true})
    } else {
      listCat.push({name: categoryStr, checked: false})
    }
  }
  //
  setFilters([{
    name: "Catégories",
    lists: listCat
  }])
}
//
export const getFilteredCategories = () => {
  let filtersStr = localStorage.getItem("filters");
  if (filtersStr != null) {
    let filters: Filter[] = JSON.parse(filtersStr);
    let listCat = filters.find(p => p.name == "Catégories")?.lists
    if (listCat != null)
    {
      let selectedCat = listCat.filter(p => p.checked).map((f) => f.name)
      if (selectedCat.length > 0) return selectedCat;
    }
  }
  return getSavedCategories();
}
export const getSavedCategories = () => {
  let filtersStr = localStorage.getItem("filters");
  if (filtersStr != null) {
    let filters: Filter[] = JSON.parse(filtersStr);
    let listCat = filters.find(p => p.name == "Catégories")?.lists
    if (listCat != null)
    {
      return listCat.map((f) => f.name)
    }
  }
  return [];
}
//

export const filters: Ref<Filter[]> = ref(getFilters());

export const filterCheckedCount = computed(() => {
  return filters.value
    .map((filter) => getCheckedValue(filter))
    .flat()
    .filter((val) => val).length;
});

function getCheckedValue(filter: Filter) {
  return filter.lists.map((value) => {
    if (value.checked) {
      return value.checked;
    }
  });
}

export const changeCheckedFilterValue = (
  value: FilterValue,
  propsName: string
) => {
  filters.value.forEach((filter) => {
    if (filter.name === propsName) {
      filter.lists.forEach((list) => {
        if (list.name === value.name) {
          list.checked = !list.checked;
        }
      });
    }
  });
  setFilters(filters.value);
};

export const ResetFilter = () => {
  filters.value.forEach((filter) => {
    filter.lists.forEach((list) => (list.checked = false));
  });
  setFilters(filters.value);
};

export const mobileFilterOption = ref(false);

export const openFilterOption = () => {
  mobileFilterOption.value = true;
  document.body.style.overflow = "hidden";
};

export const closeFilterOption = () => {
  mobileFilterOption.value = false;
  document.body.style.overflow = "auto";
};
