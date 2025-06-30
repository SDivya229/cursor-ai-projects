import { useCallback } from 'react';
import { useCartContext } from './CartContextProvider';
import useCartTotal from './useCartTotal';
import { ICartProduct } from 'models';

const useCartProducts = () => {
  const { products, setProducts } = useCartContext();
  const { updateCartTotal } = useCartTotal();

  const updateQuantitySafely = (
    currentProduct: ICartProduct,
    targetProduct: ICartProduct,
    quantity: number
  ): ICartProduct => {
    if (currentProduct.id === targetProduct.id) {
      return Object.assign({
        ...currentProduct,
        quantity: currentProduct.quantity + quantity,
      });
    } else {
      return currentProduct;
    }
  };

  const addProduct = useCallback((newProduct: ICartProduct) => {
    // Find the index of the product in the cart
    const productIndex = products.findIndex(
      (product: ICartProduct) => newProduct.id === product.id
    );

    let updatedProducts;

    if (productIndex !== -1) {
      // Product exists: create a new array with updated quantity
      updatedProducts = products.map((product, idx) =>
        idx === productIndex
          ? { ...product, quantity: product.quantity + newProduct.quantity }
          : product
      );
    } else {
      // Product does not exist: add a new product (clone to avoid mutating props)
      updatedProducts = [...products, { ...newProduct }];
    }

    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  }, [products, setProducts, updateCartTotal]);

  const removeProduct = (productToRemove: ICartProduct) => {
    const updatedProducts = products.filter(
      (product: ICartProduct) => product.id !== productToRemove.id
    );

    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  };

  const increaseProductQuantity = (productToIncrease: ICartProduct) => {
    const updatedProducts = products.map((product: ICartProduct) => {
      return updateQuantitySafely(product, productToIncrease, +1);
    });

    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  };

  const decreaseProductQuantity = (productToDecrease: ICartProduct) => {
    const updatedProducts = products.map((product: ICartProduct) => {
      return updateQuantitySafely(product, productToDecrease, -1);
    });

    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  };

  return {
    products,
    addProduct,
    removeProduct,
    increaseProductQuantity,
    decreaseProductQuantity,
  };
};

export default useCartProducts;
